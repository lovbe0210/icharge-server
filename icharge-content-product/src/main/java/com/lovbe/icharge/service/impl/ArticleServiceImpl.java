package com.lovbe.icharge.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.yitter.idgen.YitIdHelper;
import com.lovbe.icharge.common.config.ServiceProperties;
import com.lovbe.icharge.common.enums.CommonStatusEnum;
import com.lovbe.icharge.common.enums.EncorageBehaviorEnum;
import com.lovbe.icharge.common.enums.SysConstant;
import com.lovbe.icharge.common.exception.GlobalErrorCodes;
import com.lovbe.icharge.common.exception.ServiceErrorCodes;
import com.lovbe.icharge.common.exception.ServiceException;
import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.common.model.dto.*;
import com.lovbe.icharge.common.service.CommonService;
import com.lovbe.icharge.common.util.CommonUtils;
import com.lovbe.icharge.common.util.JsonUtils;
import com.lovbe.icharge.common.util.redis.RedisKeyConstant;
import com.lovbe.icharge.common.util.redis.RedisUtil;
import com.lovbe.icharge.dao.ArticleDao;
import com.lovbe.icharge.dao.ColumnDao;
import com.lovbe.icharge.dao.ContentDao;
import com.lovbe.icharge.dao.CreateRecordDao;
import com.lovbe.icharge.entity.dto.*;
import com.lovbe.icharge.entity.vo.ArticleVo;
import com.lovbe.icharge.entity.vo.ContentVo;
import com.lovbe.icharge.service.ArticleService;
import com.lovbe.icharge.service.feign.IndividuationService;
import com.lovbe.icharge.service.feign.StorageService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author: lovbe0210
 * @Date: 2024/10/9 23:54
 * @Description: MS
 */
@Slf4j
@Service
public class ArticleServiceImpl implements ArticleService {
    @Resource
    private ArticleDao articleDao;
    @Resource
    private ContentDao contentDao;
    @Resource
    private StorageService storageService;
    @Resource
    private ColumnDao columnDao;
    @Resource
    private CreateRecordDao createRecordDao;
    @Resource
    private CommonService commonService;
    @Resource
    private IndividuationService inService;
    @Resource
    private ServiceProperties serviceProperties;
    // 文档，专栏，随笔，阅读
    @Value("${spring.kafka.topics.action-content-publish}")
    private String publishActionTopic;
    @Value("${spring.application.name}")
    private String appName;

    @Override
    public ArticleVo createBlankDoc(Long columnId, long userId) {
        ArticleDo articleDo = new ArticleDo();
        if (columnId != null) {
            ColumnDo columnDo = columnDao.selectById(columnId);
            if (columnDo == null || CommonStatusEnum.isDelete(columnDo.getStatus())) {
                throw new ServiceException(ServiceErrorCodes.COLUMN_NOT_EXIST);
            }
            if (CommonStatusEnum.isDisable(columnDo.getStatus())) {
                throw new ServiceException(ServiceErrorCodes.COLUMN_STATUS_ERROR);
            }
            articleDo.setIsPublic(columnDo.getIsPublic());
        } else {
            ResponseBean<PreferenceSettingVo> preferenceSetting = inService.getPreferenceSetting(userId);
            if (preferenceSetting != null && preferenceSetting.isResult() && preferenceSetting.getData().getContentDefaultPublic() == 0) {
                articleDo.setIsPublic(0);
            } else {
                articleDo.setIsPublic(1);
            }
        }
        articleDo.setUid(YitIdHelper.nextId())
                .setCreateTime(new Date())
                .setUpdateTime(new Date());
        articleDo.setUserId(userId)
                .setTitle("无标题文档")
                .setPublishStatus(SysConstant.PUBLISH_WAIT)
                .setColumnId(columnId)
                .setUri(commonService.getBeautifulId());
        articleDao.insertOrUpdate(articleDo);
        ArticleVo articleVO = new ArticleVo();
        BeanUtil.copyProperties(articleDo, articleVO);
        return articleVO;
    }

    @Override
    public ArticleVo getArticleForEdit(String uri, long userId) {
        ArticleDo articleDo = articleDao.selectOne(new LambdaQueryWrapper<ArticleDo>()
                .eq(ArticleDo::getUri, uri)
                .eq(ArticleDo::getUserId, userId));
        if (articleDo == null || !CommonStatusEnum.isNormal(articleDo.getStatus())) {
            throw new ServiceException(ServiceErrorCodes.ARTICLE_NOT_EXIST);
        }
        if (CommonStatusEnum.isDisable(articleDo.getStatus())) {
            throw new ServiceException(ServiceErrorCodes.ARTICLE_STATUS_ERROR);
        }
        ArticleVo articleVO = new ArticleVo();
        BeanUtil.copyProperties(articleDo, articleVO);
        if (articleDo.getColumnId() == null) {
            return articleVO;
        }
        ColumnDo columnDo = columnDao.selectById(articleDo.getColumnId());
        if (columnDo == null || !CommonStatusEnum.isNormal(columnDo.getStatus())) {
            articleVO.setColumnId(null);
        } else {
            articleVO.setColumnName(columnDo.getTitle());
        }
        return articleVO;
    }

    @Override
    public void updateArticle(boolean simpleUpdate, ArticleDTO articleDTO, long userId) {
        ArticleDo articleDo = articleDao.selectById(articleDTO.getUid());
        checkArticleStatus(userId, articleDo);
        String tags = articleDTO.getTagsArray();
        if (StringUtils.hasLength(tags)) {
            articleDo.setTags(JSONUtil.toList(tags, Map.class));
        }
        articleDo.setUpdateTime(new Date());
        ArticleEsEntity esEntity = new ArticleEsEntity()
                .setUid(articleDo.getUid())
                .setTitle(articleDo.getTitle())
                .setSummary(articleDo.getSummary())
                .setIsPublic(articleDo.getIsPublic())
                .setFirstCategory(articleDo.getFirstCategory())
                .setSecondCategory(articleDo.getSecondCategory());
        BeanUtil.copyProperties(articleDTO, articleDo);
        // 判断是否需要更新封面文件
        if (!simpleUpdate && articleDTO.getCoverFile() != null) {
            CommonUtils.checkUploadFrequencyLimit(String.valueOf(userId),
                    SysConstant.FILE_SCENE_COVER, serviceProperties.getCoverUploadLimit());
            // 上传文件
            ResponseBean<String> upload = storageService
                    .upload(new FileUploadDTO(articleDTO.getCoverFile(), SysConstant.FILE_SCENE_COVER, String.valueOf(userId)));
            if (!upload.isResult()) {
                log.error("[更新文章信息] --- 封面上传失败，errorInfo: {}", upload.getMessage());
                throw new ServiceException(ServiceErrorCodes.ARTICLE_INFO_UPDATE_FAILED);
            }
            articleDo.setCoverUrl(upload.getData());
        }
        if (StringUtils.hasLength(articleDTO.getFirstCategory()) && !StringUtils.hasLength(articleDTO.getSecondCategory())) {
            articleDo.setSecondCategory("");
        }
        articleDao.updateById(articleDo);
        // 同步更新es数据
        if (articleDTO.getTitle() != null) {
            esEntity.setTitle(articleDTO.getTitle());
        }
        if (articleDTO.getSummary() != null) {
            esEntity.setSummary(articleDTO.getSummary());
        }
        if (articleDTO.getIsPublic() != null) {
            esEntity.setIsPublic(articleDTO.getIsPublic());
        }
        if (articleDTO.getFirstCategory() != null) {
            esEntity.setFirstCategory(articleDTO.getFirstCategory());
            if (articleDTO.getSecondCategory() != null) {
                esEntity.setSecondCategory(articleDTO.getSecondCategory());
            } else {
              esEntity.setSecondCategory("");
            }
        }
        List<Map> userTags = articleDo.getTags();
        if (!CollectionUtils.isEmpty(userTags)) {
            StringBuilder tagStr = new StringBuilder();
            for (Map tag : userTags) {
                if (tagStr.length() > 0) {
                    tagStr.append(",");
                }
                tagStr.append(tag.get(SysConstant.TAG_FIELD_CONTENT));
            }
            esEntity.setUserTags(tagStr.toString());
        }
        commonService.updateElasticsearchArticle(Arrays.asList(esEntity));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map updateContent(BaseRequest<ContentDTO> contentEntity, long userId) {
        ContentDTO contentDTO = contentEntity.getData();
        ArticleDo articleDo = articleDao.selectById(contentDTO.getTargetId());
        checkArticleStatus(userId, articleDo);
        ContentDo contentDo = new ContentDo();
        Long uid = contentDTO.getUid();
        Date updateTime = new Date();
        HashMap<String, Object> map = MapUtil.of(SysConstant.UPDATE_TIME, updateTime);
        if (uid == null) {
            uid = YitIdHelper.nextId();
            contentDo.setCreateTime(updateTime);
        }
        contentDo.setUid(uid).setUpdateTime(updateTime);
        contentDo.setContent(JSONUtil.toJsonStr(contentDTO.getContent()));
        contentDao.insertOrUpdate(contentDo);

        // 判断是否需要更新文档信息
        if (articleDo.getWordsNum() == 0 && contentDTO.getWordsNum() == 0) {
            map.put(SysConstant.CONTENT_ID, uid);
            return map;
        }

        // 今日写作+5经验
        String dailyEncourageKey = RedisKeyConstant.getUserdailyEncourage(userId, SysConstant.LEVEL_ENCOURAGE_WRITE);
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 999);
        boolean absent = RedisUtil.setnx(dailyEncourageKey);
        if (absent) {
            RedisUtil.setExpireAt(dailyEncourageKey, c.getTime());
            commonService.updateUserLevel(userId, 5);
        }

        // 是否需要更新摘要
        if (articleDo.getAutoSummary() == 1) {
            articleDo.setSummary(contentDTO.getSummary());
        }
        // 是否需要更新封面
        if (!StringUtils.hasLength(articleDo.getCoverUrl())) {
            articleDo.setCoverUrl(contentDTO.getCoverUrl());
        }

        // 判断文章是否开启自动发布
        if (articleDo.getIsPublic() == 1) {
            Integer autoPublish = articleDao.selectEnableAutoPublish(articleDo.getUid());
            if (autoPublish == null || autoPublish == 1) {
                // 发送审核消息
                commonService.sendMessage(appName, publishActionTopic,
                        new ContentPublishDTO(articleDo.getUid(), SysConstant.TARGET_TYPE_ARTICLE, uid, updateTime)
                );
                articleDo.setPublishStatus(SysConstant.PUBLISH_AUDIT);
            } else {
                articleDo.setPublishStatus(SysConstant.PUBLISH_WAIT);
            }
        } else {
            articleDo.setPublishStatus(SysConstant.PUBLISH_WAIT);
        }

        // 入库
        articleDo.setUpdateTime(updateTime);
        articleDo.setLatestContentId(uid)
                .setWordsNum(contentDTO.getWordsNum());
        articleDao.updateById(articleDo);
        map.put(SysConstant.CONTENT_ID, uid);
        return map;
    }

    @Override
    public List<ArticleVo> getMyArticleList(BaseRequest<RequestListDTO> requestDto, long userId) {
        RequestListDTO data = requestDto.getData();
        LambdaQueryWrapper<ArticleDo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ArticleDo::getUserId, userId)
                .eq(ArticleDo::getStatus, CommonStatusEnum.NORMAL.getStatus())
                .isNull(ArticleDo::getColumnId)
                .like(data != null && StringUtils.hasLength(data.getKeywords()), ArticleDo::getTitle, data.getKeywords())
                .orderByDesc(ArticleDo::getSort);
        if (data != null && data.getSort() != null) {
            queryWrapper.orderByDesc(data.getSort() == 1, ArticleDo::getUpdateTime);
            queryWrapper.orderByDesc(data.getSort() == 2, ArticleDo::getCreateTime);
        } else {
            queryWrapper.orderByAsc(ArticleDo::getUid);
        }
        List<ArticleDo> selectList = articleDao.selectList(queryWrapper);
        if (!CollectionUtils.isEmpty(selectList)) {
            return selectList.stream().map(articleDo -> {
                ArticleVo articleVO = new ArticleVo();
                BeanUtil.copyProperties(articleDo, articleVO);
                return articleVO;
            }).collect(Collectors.toList());
        }
        return List.of();
    }

    @Override
    public ContentVo getContent(Long articleId, long userId) {
        ArticleDo articleDo = articleDao.selectById(articleId);
        checkArticleStatus(userId, articleDo);
        ContentDo contentDo = contentDao.selectById(articleDo.getLatestContentId());
        if (contentDo == null) {
            return null;
        }
        ContentVo contentVO = new ContentVo(articleDo.getLatestContentId(), contentDo.getContent(), articleId);
        return contentVO;
    }

    @Override
    public void updateArticleTop(BaseRequest<ArticleDTO> requestDto, long userId) {
        ArticleDTO articleDTO = requestDto.getData();
        ArticleDo articleDo = articleDao.selectById(articleDTO.getUid());
        checkArticleStatus(userId, articleDo);
        articleDao.updateArticleTop(articleDTO.getUid());
    }

    @Override
    public void publishArticle(Long articleId, long userId) {
        ArticleDo articleDo = articleDao.selectById(articleId);
        checkArticleStatus(userId, articleDo);
        // 如果是私有状态则发布失败
        if (articleDo.getIsPublic() != null && articleDo.getIsPublic() == 0) {
            throw new ServiceException(ServiceErrorCodes.ARTICLE_PUBLISH_FAILED);
        }
        // 获取最后一次编辑的正文信息
        ContentDo contentDo = contentDao.selectById(articleDo.getLatestContentId());
        if (contentDo == null) {
            throw new ServiceException(ServiceErrorCodes.ARTICLE_EMPTY_PUBLISH_FAILED);
        }
        // 判断状态：只有在文档内容更新时才能二次发布
        Integer publishStatus = articleDo.getPublishStatus();
        if (publishStatus == null || publishStatus != 0) {
            return;
        }
        articleDo.setPublishStatus(1);
        articleDao.updateById(articleDo);
        // 发送消息进行内容审核（发布n次，审核n次，这样才能保证内容不会遗漏）
        commonService.sendMessage(appName, publishActionTopic,
                new ContentPublishDTO(articleDo.getUid(),
                        SysConstant.TARGET_TYPE_ARTICLE,
                        articleDo.getLatestContentId(),
                        contentDo.getUpdateTime()));
    }

    @Override
    public void deleteArticle(Long articleId, long userId) {
        ArticleDo articleDo = articleDao.selectById(articleId);
        if (articleDo == null) {
            throw new ServiceException(ServiceErrorCodes.ARTICLE_NOT_EXIST);
        }
        if (articleDo.getUserId() != userId) {
            throw new ServiceException(GlobalErrorCodes.LOCKED);
        }
        articleDo.setStatus(CommonStatusEnum.DELETE.getStatus());
        articleDao.updateById(articleDo);
        // 删除Elasticsearch中的数据
        commonService.deleteElasticsearchArticle(Arrays.asList(String.valueOf(articleId)));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void articleBatchOperate(BaseRequest<ArticleOperateDTO> requestDto, long userId) {
        // 获取文章信息判断用于状态判断
        ArticleOperateDTO data = requestDto.getData();
        List<ArticleDo> articleList = articleDao.selectBatchIds(data.getArticleList());
        List<String> articleIds = new ArrayList<>();
        articleList = articleList.stream()
                .peek(articleDo -> articleIds.add(String.valueOf(articleDo.getUid())))
                .filter(article -> CommonStatusEnum.isNormal(article.getStatus())
                        && Objects.equals(article.getUserId(), userId))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(articleList)) {
            throw new ServiceException(ServiceErrorCodes.ARTICLE_NOT_EXIST);
        }

        // 批量发布
        if (SysConstant.ARTICLE_BATCH_PUBLISH.equals(data.getOperateType())) {
            batchPublish(articleList);
            return;
        }

        // 批量移至专栏，需要继承专栏的权限
        if (Objects.equals(SysConstant.ARTICLE_BATCH_MOVE, data.getOperateType())) {
            ColumnDo columnDo = columnDao.selectById(data.getColumnId());
            Assert.notNull(columnDo, ServiceErrorCodes.COLUMN_NOT_EXIST.getMsg());
            articleDao.batchUpdate(articleList, data.getColumnId(), columnDo.getIsPublic(), data.getOperateType());
            // 更新es状态数据
            List<ArticleEsEntity> esEntityList = articleList.stream()
                    .map(article -> new ArticleEsEntity()
                            .setUid(article.getUid())
                            .setIsPublic(columnDo.getIsPublic())
                    )
                    .collect(Collectors.toList());
            commonService.updateElasticsearchArticle(esEntityList);
            return;
        }

        // 批量删除，需要对应删除es数据
        if (Objects.equals(SysConstant.ARTICLE_BATCH_DELETE, data.getOperateType())) {
            articleDao.batchUpdate(articleList, data.getColumnId(), null, data.getOperateType());
            commonService.deleteElasticsearchArticle(articleIds);
        }
    }

   @Override
    public void batchPublish(List<ArticleDo> articleList) {
        List<Long> contentIs = new ArrayList<>();
        List<ArticleDo> collect = articleList.stream()
                .filter(article -> article.getIsPublic() != null &&
                        article.getLatestContentId() != null &&
                        article.getIsPublic() == 1 &&
                        article.getPublishStatus() != null &&
                        article.getPublishStatus() == 0)
                .peek(article -> contentIs.add(article.getLatestContentId()))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(collect) || contentIs.size() == 0) {
            throw new ServiceException(ServiceErrorCodes.ARTICLE_PUBLISH_FAILED2);
        }
        // 获取文章内容信息
        List<ContentDo> contentList = contentDao.selectBatchIds(contentIs);
        if (CollectionUtils.isEmpty(contentList)) {
            throw new ServiceException(ServiceErrorCodes.ARTICLE_EMPTY_PUBLISH_FAILED);
        }
        Map<Long, ContentDo> contentMap = contentList.stream()
                .collect(Collectors.toMap(ContentDo::getUid, Function.identity(), (a, b) -> b));
        // 过滤没有内容的文章
        collect = collect.stream()
                .filter(articleDo -> contentMap.containsKey(articleDo.getLatestContentId()))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(collect)) {
            throw new ServiceException(ServiceErrorCodes.ARTICLE_EMPTY_PUBLISH_FAILED);
        }
        articleDao.batchUpdate(collect, null, null, SysConstant.ARTICLE_BATCH_PUBLISH);
        // 发送消息
        for (ArticleDo articleDo : collect) {
            ContentDo contentDo = contentMap.get(articleDo.getLatestContentId());
            commonService.sendMessage(appName, publishActionTopic,
                    new ContentPublishDTO(articleDo.getUid(),
                            SysConstant.TARGET_TYPE_ARTICLE,
                            articleDo.getLatestContentId(),
                            contentDo.getUpdateTime()));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<Long, ArticleVo> copyArticle(ArticleOperateDTO data, long userId) {
        List<ArticleDo> selectedList = articleDao.selectList(new LambdaQueryWrapper<ArticleDo>()
                .eq(ArticleDo::getColumnId, data.getColumnId())
                .in(ArticleDo::getUid, data.getArticleList())
                .eq(ArticleDo::getStatus, CommonStatusEnum.NORMAL.getStatus())
                .orderByDesc(ArticleDo::getUpdateTime));
        if (CollectionUtils.isEmpty(selectedList)) {
            throw new ServiceException(ServiceErrorCodes.ARTICLE_NOT_EXIST);
        }
        Map<Long, ArticleVo> map = new HashMap<>();
        Map<Long, Long> newContent = new HashMap<>(selectedList.size());
        Date createTime = new Date();

        // 复制新文章
        selectedList.forEach(articleDo -> {
            Long uid = articleDo.getUid();
            articleDo.setUid(YitIdHelper.nextId())
                    .setCreateTime(createTime)
                    .setUpdateTime(createTime);
            articleDo.setTitle(articleDo.getTitle() + " 副本")
                    .setUri(commonService.getBeautifulId())
                    .setPublishStatus(0);
            if (articleDo.getLatestContentId() != null) {
                long newId = YitIdHelper.nextId();
                newContent.put(articleDo.getLatestContentId(), newId);
                articleDo.setLatestContentId(newId);
            }
            ArticleVo articleVO = new ArticleVo();
            BeanUtil.copyProperties(articleDo, articleVO);
            map.put(uid, articleVO);
        });

        // 如果内容为空，则直接保存文章返回结果
        if (newContent.size() == 0) {
            articleDao.insertOrUpdate(selectedList);
            return map;
        }

        // 获取文章内容
        List<ContentDo> contentDos = contentDao.selectList(new LambdaQueryWrapper<ContentDo>()
                .in(ContentDo::getUid, newContent.keySet())
                .eq(ContentDo::getStatus, CommonStatusEnum.NORMAL.getStatus()));
        Map<Long, ContentDo> contentMap = contentDos.stream()
                .collect(Collectors.toMap(ContentDo::getUid, Function.identity()));
        HashSet<Long> newIds = new HashSet<>();
        newContent.keySet().forEach(oldId -> {
            ContentDo contentDo = contentMap.get(oldId);
            if (contentDo != null) {
                contentDo.setUid(newContent.get(oldId))
                        .setCreateTime(createTime)
                        .setUpdateTime(createTime);
                newIds.add(newContent.get(oldId));
            }
        });

        // contentId回填
        selectedList.forEach(articleDo -> {
            Long latestContentId = articleDo.getLatestContentId();
            if (latestContentId != null && !newIds.contains(latestContentId)) {
                articleDo.setLatestContentId(null);
            }
        });
        map.values().forEach(articleVO -> {
            Long latestContentId = articleVO.getLatestContentId();
            if (latestContentId != null && !newIds.contains(latestContentId)) {
                articleVO.setLatestContentId(null);
            }
        });

        // 入库
        articleDao.insertOrUpdate(selectedList);
        contentDao.insertOrUpdate(contentDos, 20);
        return map;
    }

    @Override
    public void handlerPublishAction(List<ContentPublishDTO> collect) {
        // 对同一target进行过滤，只审核一次，取最新的contentId即可
        Map<Long, ContentPublishDTO> publishDTOMap = collect.stream()
                .collect(Collectors.toMap(ContentPublishDTO::getTargetId,
                        Function.identity(),
                        (a, b) -> b.getPublishTime().after(a.getPublishTime()) ? b : a
                ));

        // 获取当前批量的contentId进行批量查询
        Set<Long> contentIds = new HashSet<>();
        publishDTOMap.values().forEach(publishDTO -> contentIds.add(publishDTO.getContentId()));
        List<ContentDo> contentList = contentDao.selectBatchIds(contentIds);
        HashMap<Long, ContentDo> contentMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(contentList)) {
            contentMap.putAll(contentList.stream().collect(Collectors.toMap(ContentDo::getUid, Function.identity())));
        }
        for (ContentPublishDTO publishDTO : publishDTOMap.values()) {
            // 获取最新id内容进行审核
            ContentDo contentDo = contentMap.get(publishDTO.getContentId());
            if (contentDo == null || !StringUtils.hasLength(contentDo.getContent())) {
                continue;
            }
            try {
                // 对内容进行解析，获取纯文本内容
                JSONObject parseObj = JSONUtil.parseObj(contentDo.getContent());
                String textValue = CommonUtils.getContentTextValue(parseObj);
                if (log.isDebugEnabled()) {
                    log.debug("[文章内容审核] --- textValue: {}", textValue);
                }
                // 发送文章内容审核请求
                AIAuditResultDTO resultDto = commonService.sendAuditChat(SysConstant.TARGET_TYPE_ARTICLE, textValue);
                if (resultDto == null) {
                    log.error("[文章内容审核] --- 大模型审核结果为空，请在日志中查看详细错误");
                    // TODO 对于审核异常的需要放入死信队列手动审核
                    continue;
                }
                // 获取文章title、category等其他搜索字段
                ArticleDo articleDo = articleDao.selectById(publishDTO.getTargetId());
                // 结果解析ok
                if (resultDto != null && resultDto.isResult()) {
                    log.info("[文章内容审核] --- 大模型审核通过");
                    // 根据发布时间contentId更新发布状态
                    int update = articleDao.updateByPublishContent(publishDTO, SysConstant.PUBLISH_SUCCESS);
                    // 说明自上次发布后再无修改
                    if (update != 0) {
                        // 文章信息录入Elasticsearch
                        ArticleEsEntity articleEsEntity = new ArticleEsEntity()
                                .setUid(publishDTO.getTargetId())
                                .setContent(textValue);
                        if (articleDo != null) {
                            articleEsEntity.setTitle(articleDo.getTitle())
                                    .setSummary(articleDo.getSummary())
                                    .setIsPublic(articleDo.getIsPublic())
                                    .setFirstCategory(articleDo.getFirstCategory())
                                    .setSecondCategory(articleDo.getSecondCategory());
                            List<Map> tags = articleDo.getTags();
                            if (!CollectionUtils.isEmpty(tags)) {
                                StringBuilder tagStr = new StringBuilder();
                                for (Map tag : tags) {
                                    if (tagStr.length() > 0) {
                                        tagStr.append(",");
                                    }
                                    tagStr.append(tag.get(SysConstant.TAG_FIELD_CONTENT));
                                }
                                articleEsEntity.setUserTags(tagStr.toString());
                            }
                        }
                        List<String> tags = resultDto.getTags();
                        if (!CollectionUtils.isEmpty(tags)) {
                            articleEsEntity.setCategory(tags.get(0) + (tags.size() > 1 ? ("," + tags.get(1)) : ""));
                            if (tags.size() > 2) {
                                List<String> subList = tags.subList(2, tags.size());
                                articleEsEntity.setTags(StringUtils.collectionToDelimitedString(subList, ","));
                            }
                        }
                        // 审核通过，更新elasticsearch
                        commonService.updateElasticsearchArticle(Arrays.asList(articleEsEntity));
                        // 审核通过，更新创作记录
                        CreateRecordDo recordDo = new CreateRecordDo(SysConstant.TARGET_TYPE_ARTICLE, articleDo.getUserId());
                        recordDo.setUid(articleDo.getUid())
                                .setStatus(CommonStatusEnum.NORMAL.getStatus())
                                .setCreateTime(new Date())
                                .setUpdateTime(recordDo.getCreateTime());
                        createRecordDao.insertOrUpdate(recordDo);
                        if (articleDo.getPublishTime() == null) {
                            // 发布成功，如果是该篇文章首次发布，增加激励电池
                            commonService.saveEncourageLog(articleDo.getUserId(), articleDo.getUid(), articleDo.getTitle(), EncorageBehaviorEnum.BEHAVIOR_PUBLISH);
                        }
                    }
                } else if (resultDto != null && !resultDto.isResult()) {
                    List<String> reasonList = resultDto.getReason();
                    log.info("[文章内容审核] --- 大模型审核失败, reason: {}", reasonList);
                    articleDao.updateByPublishContent(publishDTO, SysConstant.PUBLISH_FAILED);
                    // 记录审核失败通知
                    String noticeContent = "文章发布失败，公开发布内容需符合本站创作内容约定";
                    if (!CollectionUtils.isEmpty(reasonList)) {
                        StringBuilder tmp = new StringBuilder();
                        for (int i = 0; i < reasonList.size(); i++) {
                            String reason = reasonList.get(i);
                            if (reason != null && reason.contains("reason")) {
                                try {
                                    JSONObject entries = JsonUtils.parseObject(reason, JSONObject.class);
                                    tmp.append("\"" + entries.getStr("content") + "\"");
                                    tmp.append(entries.getStr("reason"));
                                }catch (Exception e) {
                                    log.error("");
                                }
                            } else if (reason != null) {
                                tmp.append(reason);
                            }
                            if (i != reasonList.size() - 1) {
                                tmp.append(";");
                            }
                        }
                        if (tmp.length() > 0) {
                            noticeContent = tmp.toString();
                        }
                    }
                    SocialNoticeDo noticeDo = new SocialNoticeDo()
                            .setTargetId(publishDTO.getTargetId())
                            .setUserId(articleDo.getUserId())
                            .setNoticeType(SysConstant.NOTICE_AUDIT_ARTICLE)
                            .setActionUserId(0L)
                            .setNoticeContent(noticeContent);
                    noticeDo.setUid(YitIdHelper.nextId());
                    articleDao.insertAuditNotice(noticeDo);
                }
            } catch (Exception e) {
                log.error("[文章内容审核] --- 正文内容解析失败，contentId: {}, errorInfo: {}", publishDTO.getContentId(), e.toString());
            }
        }
    }

    /**
     * @description: 文章状态判断
     * @param: long
     * @return: void
     * @author: lovbe0210
     * @date: 2025/1/5 11:05
     */
    private static void checkArticleStatus(long userId, ArticleDo articleDo) {
        if (articleDo == null) {
            throw new ServiceException(ServiceErrorCodes.ARTICLE_NOT_EXIST);
        }
        if (!CommonStatusEnum.isNormal(articleDo.getStatus())) {
            throw new ServiceException(ServiceErrorCodes.ARTICLE_STATUS_ERROR);
        }
        if (articleDo.getUserId() != userId) {
            throw new ServiceException(GlobalErrorCodes.LOCKED);
        }
    }
}
