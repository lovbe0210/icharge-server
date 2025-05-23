package com.lovbe.icharge.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.yitter.idgen.YitIdHelper;
import com.lovbe.icharge.common.enums.CommonStatusEnum;
import com.lovbe.icharge.common.exception.GlobalErrorCodes;
import com.lovbe.icharge.common.exception.ServiceErrorCodes;
import com.lovbe.icharge.common.exception.ServiceException;
import com.lovbe.icharge.common.model.base.KafkaMessage;
import com.lovbe.icharge.common.model.dto.ArticleDo;
import com.lovbe.icharge.common.model.dto.ColumnDo;
import com.lovbe.icharge.common.service.CommonService;
import com.lovbe.icharge.dao.CollectDao;
import com.lovbe.icharge.dao.CollectTagDao;
import com.lovbe.icharge.dao.PublicContentDao;
import com.lovbe.icharge.entity.dto.*;
import com.lovbe.icharge.entity.vo.CollectVo;
import com.lovbe.icharge.service.CollectService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.text.Collator;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author: lovbe0210
 * @Date: 2024/11/25 23:19
 * @Description: 收藏夹
 */
@Slf4j
@Service
public class CollectServiceImpl implements CollectService {
    @Resource
    private CollectDao collectDao;
    @Resource
    private CollectTagDao collectTagDao;
    @Resource
    private PublicContentDao publicContentDao;
    @Resource
    private CommonService commonService;
    // 文档，专栏，随笔，阅读
    @Value("${spring.kafka.topics.user-action-collect}")
    private String collectActionTopic;
    @Value("${spring.application.name}")
    private String appName;

    @Override
    public Long marksContent(CollectTargetDTO data, Long userId) {
        CollectDo collectDb = collectDao.selectOne(new LambdaQueryWrapper<CollectDo>()
                .eq(CollectDo::getUserId, userId)
                .eq(CollectDo::getTargetId, data.getTargetId()));
        if (collectDb != null) {
            // 更新
            collectDb.setTags(data.getTags())
                    .setUpdateTime(new Date())
                    .setStatus(CommonStatusEnum.NORMAL.getStatus());
            collectDao.insertOrUpdate(collectDb);
            return userId;
        }
        if (data.getUid() == null) {
            data.setUid(YitIdHelper.nextId());
        }
        CollectDo collectDo = new CollectDo();
        BeanUtil.copyProperties(data, collectDo);
        collectDo.setUid(data.getUid())
                .setStatus(CommonStatusEnum.NORMAL.getStatus())
                .setCreateTime(new Date())
                .setUpdateTime(new Date());
        collectDo.setUserId(userId);
        collectDao.insertOrUpdate(collectDo);
        // 添加收藏统计
        sendCollectMessage(data.getTargetId(), data.getTargetType(), 1);
        return collectDo.getUid();
    }

    @Override
    public void cancelMarkContent(Long ftId, Long userId) {
        CollectDo collectDo = collectDao.selectById(ftId);
        if (collectDo == null || CommonStatusEnum.isDelete(collectDo.getStatus())) {
            return;
        }
        if (!Objects.equals(collectDo.getUserId(), userId)) {
            throw new ServiceException(GlobalErrorCodes.BAD_REQUEST);
        }
        collectDo.setStatus(CommonStatusEnum.DELETE.getStatus());
        int updated = collectDao.updateById(collectDo);
        if (updated < 1) {
            log.error("[收藏夹] --- 取消收藏失败，ftId：{}，userId：{}", ftId, userId);
        } else {
            // 发送消息取消收藏统计
            sendCollectMessage(collectDo.getTargetId(), collectDo.getTargetType(), 0);
        }
    }

    @Override
    public List<CollectTagsDTO> getCollectTagCount(Long userId) {
        List<CollectTagsDTO> collectTagList = collectTagDao.selectList(new LambdaQueryWrapper<CollectTagsDTO>()
                .eq(CollectTagsDTO::getUserId, userId)
                .eq(CollectTagsDTO::getStatus, CommonStatusEnum.NORMAL.getStatus())
                .orderByDesc(CollectTagsDTO::getUpdateTime));
        collectTagList = CollectionUtils.isEmpty(collectTagList) ? new ArrayList<>() : collectTagList;
        CollectTagsDTO defaultTag = new CollectTagsDTO().setTitle("全部收藏");
        defaultTag.setUid(-1L);
        collectTagList.add(0, defaultTag);
        // 获取所有收藏内容
        List<CollectDo> collectList = collectDao.selectList(new LambdaQueryWrapper<CollectDo>()
                .eq(CollectDo::getStatus, CommonStatusEnum.NORMAL.getStatus())
                .eq(CollectDo::getUserId, userId));
        if (CollectionUtils.isEmpty(collectList)) {
            return collectTagList;
        }
        defaultTag.setCollectCount(collectList.size());
        Map<Long, CollectTagsDTO> collectTagMap = collectTagList.stream()
                .collect(Collectors.toMap(CollectTagsDTO::getUid, Function.identity()));
        collectList.forEach(collectDo -> {
            Set<Long> tags = collectDo.getTags();
            if (!CollectionUtils.isEmpty(tags)) {
                tags.forEach(tag -> {
                    CollectTagsDTO collectTag = collectTagMap.get(tag);
                    if (collectTag != null) {
                        collectTag.setCollectCount(collectTag.getCollectCount() + 1);
                    }
                });
            }
        });
        return collectTagList;
    }

    @Override
    public List<CollectTagsDTO> getCollectTagList(Long userId) {
        List<CollectTagsDTO> collectTagList = collectTagDao.selectList(new LambdaQueryWrapper<CollectTagsDTO>()
                .eq(CollectTagsDTO::getUserId, userId)
                .orderByDesc(CollectTagsDTO::getUpdateTime));
        if (CollectionUtils.isEmpty(collectTagList)) {
            return Collections.EMPTY_LIST;
        }
        return collectTagList;
    }

    @Override
    public Long updateCollectTag(CollectTagsDTO data, Long userId) {
        List<CollectTagsDTO> collectTagList = collectTagDao.selectList(
                new LambdaQueryWrapper<CollectTagsDTO>().eq(CollectTagsDTO::getUserId, userId));
        if (!CollectionUtils.isEmpty(collectTagList)) {
            collectTagList.forEach(tag -> {
                if (Objects.equals(tag.getTitle(), data.getTitle()) && !Objects.equals(tag.getUid(), data.getUid())) {
                    throw new ServiceException(ServiceErrorCodes.REPEAT_FAVORITE_TAG);
                }
            });
        }
        data.setUserId(userId);
        if (data.getUid() == null) {
            data.setUid(YitIdHelper.nextId())
                    .setStatus(CommonStatusEnum.NORMAL.getStatus())
                    .setCreateTime(new Date())
                    .setUpdateTime(new Date());
        } else {
            data.setUpdateTime(new Date());
        }
        collectTagDao.insertOrUpdate(data);
        return data.getUid();
    }

    @Override
    public void deleteCollectTag(CollectTagsDTO data, Long userId) {
        int delete = collectTagDao.delete(new LambdaQueryWrapper<CollectTagsDTO>()
                .eq(CollectTagsDTO::getUid, data.getUid())
                .eq(CollectTagsDTO::getUserId, userId));
        if (delete < 1) {
            log.error("[收藏夹] --- 删除收藏分组失败，数据删除为tagId：{}，userId：{}", data.getUid(), userId);
        }
    }

    @Override
    public List<CollectVo> getCollectList(CollectRequestDTO data, Long userId) {
        // 先获取所有进行关联数据
        List<CollectDo> collectList = collectDao.selectList(new LambdaQueryWrapper<CollectDo>()
                .eq(CollectDo::getUserId, userId)
                .eq(StringUtils.hasLength(data.getCollectType()), CollectDo::getTargetType, data.getCollectType())
                .eq(CollectDo::getStatus, CommonStatusEnum.NORMAL.getStatus())
                .orderByDesc(CollectDo::getUpdateTime));
        if (CollectionUtils.isEmpty(collectList)) {
            return Collections.EMPTY_LIST;
        }
        // 数据填充，获取数据流
        List<CollectVo> newCollect = fillCollectVo(collectList);
        newCollect = newCollect.stream()
                .filter(collect -> {
                    // 关键字过滤
                    if (!StringUtils.hasLength(data.getKeywords())) {
                        return true;
                    }
                    return collect.getName() != null && collect.getName().indexOf(data.getKeywords()) != -1;
                })
                .filter(collect -> {
                    // 分组标签过滤
                    Long selectTag = data.getSelectTag();
                    // 全部收藏tagId为-1
                    if (selectTag == null || selectTag == -1) {
                        return true;
                    }
                    Set<Long> tags = collect.getTags();
                    return !CollectionUtils.isEmpty(tags) && tags.contains(selectTag);
                }).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(newCollect)) {
            return Collections.EMPTY_LIST;
        }

        // 排序
        String titleSort = data.getTitleSort();
        String timestampSort = data.getTimestampSort();
        Collator collator = Collator.getInstance(Locale.CHINA);
        if (Objects.equals(titleSort, "asc")) {
            newCollect.sort(Comparator.comparing(CollectVo::getName, collator));
        } else if (Objects.equals(titleSort, "desc")) {
            newCollect.sort(Comparator.comparing(CollectVo::getName, collator).reversed());
        } else if (Objects.equals(timestampSort, "asc")) {
            newCollect.sort(Comparator.comparing(CollectVo::getCreateTime, collator));
        } else if (Objects.equals(timestampSort, "desc")) {
            newCollect.sort(Comparator.comparing(CollectVo::getCreateTime, collator).reversed());
        }
        // 分页
        if (data.getLimit() < 0 || data.getLimit() > 1000) data.setLimit(100);
        if (data.getOffset() < 0) data.setOffset(0);
        if (CollectionUtils.isEmpty(newCollect) || data.getOffset() >= newCollect.size()) {
            return Collections.EMPTY_LIST;
        }
        if (data.getOffset() == 0 && data.getLimit() >= newCollect.size()) {
            return newCollect;
        }
        List<CollectVo> subList = newCollect.subList(data.getOffset(), data.getLimit());
        return subList;
    }

    @Override
    public CollectVo getCollectInfo(Long collectId, Long userId) {
        CollectDo collectDo = collectDao.selectOne(new LambdaQueryWrapper<CollectDo>()
                .eq(CollectDo::getUid, collectId)
                .eq(CollectDo::getStatus, CommonStatusEnum.NORMAL.getStatus()));
        if (collectDo != null && !Objects.equals(collectDo.getUserId(), userId)) {
            throw new ServiceException(GlobalErrorCodes.BAD_REQUEST);
        }
        if (collectDo != null) {
            CollectVo collectVo = new CollectVo();
            BeanUtil.copyProperties(collectDo, collectVo);
            return collectVo;
        }
        return new CollectVo();
    }

    /**
     * 数据填充属性
     *
     * @param collectList
     * @return
     */
    private List<CollectVo> fillCollectVo(List<CollectDo> collectList) {
        List<Long> articleIds = new ArrayList<>();
        List<Long> columnIds = new ArrayList<>();
        collectList.forEach(collectDo -> {
            Integer targetType = collectDo.getTargetType();
            if (targetType == 1) {
                articleIds.add(collectDo.getTargetId());
            } else if (targetType == 2) {
                columnIds.add(collectDo.getTargetId());
            }
        });
        Map<Long, ArticleDo> articleMap = new HashMap<>();
        Map<Long, ColumnDo> columnMap = new HashMap<>();
        if (articleIds.size() > 0) {
            List<ArticleDo> articleList = publicContentDao.selectArticleList(articleIds);
            if (!CollectionUtils.isEmpty(articleList)) {
                articleList.forEach(articleDo -> articleMap.put(articleDo.getUid(), articleDo));
            }
        }
        if (columnIds.size() > 0) {
            List<ColumnDo> columnList = publicContentDao.selectColumnList(columnIds);
            if (!CollectionUtils.isEmpty(columnList)) {
                columnList.forEach(column -> columnMap.put(column.getUid(), column));
            }
        }
        List<CollectVo> newCollect = collectList.stream()
                .map(collect -> {
                    CollectVo collectVo = new CollectVo();
                    BeanUtil.copyProperties(collect, collectVo);
                    if (collect.getTargetType() == 1) {
                        ArticleDo articleDo = articleMap.get(collect.getTargetId());
                        if (articleDo != null) {
                            collectVo.setArticleUri(articleDo.getUri())
                                    .setName(articleDo.getTitle())
                                    .setColumnUri(articleDo.getColumnUri())
                                    .setColumnName(articleDo.getColumnName())
                                    .setDomain(articleDo.getDomain())
                                    .setUsername(articleDo.getUsername());
                        }
                    } else if (collect.getTargetType() == 2) {
                        ColumnDo columnDo = columnMap.get(collect.getTargetId());
                        if (columnDo != null) {
                            collectVo.setName(columnDo.getTitle())
                                    .setColumnUri(columnDo.getUri())
                                    .setUsername(columnDo.getUsername())
                                    .setDomain(columnDo.getDomain());
                        }
                    }
                    return collectVo;
                })
                .collect(Collectors.toList());
        return newCollect;
    }

    /**
     * @description 发送收藏统计消息
     * @param[1] userId
     * @param[2] targetId
     * @param[3] targetType
     * @author lovbe0210
     * @date 2024/11/24 0:10
     */
    public void sendCollectMessage(Long targetId, Integer targetType, int action) {
        CollectActionDTO collectDTO = new CollectActionDTO();
        Date now = new Date();
        collectDTO.setTargetType(targetType)
                .setAction(action)
                .setUid(targetId);
        collectDTO.setStatus(CommonStatusEnum.NORMAL.getStatus())
                .setCreateTime(now)
                .setUpdateTime(now);
        commonService.sendMessage(appName, collectActionTopic, collectDTO);
    }
}
