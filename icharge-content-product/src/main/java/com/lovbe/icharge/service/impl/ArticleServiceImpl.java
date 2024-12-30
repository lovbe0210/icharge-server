package com.lovbe.icharge.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.yitter.idgen.YitIdHelper;
import com.lovbe.icharge.common.enums.CommonStatusEnum;
import com.lovbe.icharge.common.enums.SysConstant;
import com.lovbe.icharge.common.exception.GlobalErrorCodes;
import com.lovbe.icharge.common.exception.ServiceErrorCodes;
import com.lovbe.icharge.common.exception.ServiceException;
import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.common.model.dto.*;
import com.lovbe.icharge.common.service.CommonService;
import com.lovbe.icharge.common.util.CommonUtils;
import com.lovbe.icharge.common.util.redis.RedisKeyConstant;
import com.lovbe.icharge.common.util.redis.RedisUtil;
import com.lovbe.icharge.dao.ArticleDao;
import com.lovbe.icharge.dao.ColumnDao;
import com.lovbe.icharge.dao.ContentDao;
import com.lovbe.icharge.entity.dto.ArticleDTO;
import com.lovbe.icharge.entity.dto.ArticleOperateDTO;
import com.lovbe.icharge.entity.dto.ContentDTO;
import com.lovbe.icharge.entity.dto.ContentPublishDTO;
import com.lovbe.icharge.entity.vo.ArticleVo;
import com.lovbe.icharge.entity.vo.ContentVo;
import com.lovbe.icharge.service.ArticleService;
import com.lovbe.icharge.service.feign.StorageService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
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
@EnableTransactionManagement
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
    private CommonService commonService;
    // 文档，专栏，随笔，阅读
    @Value("${spring.kafka.topics.action-content-publish}")
    private String publishActionTopic;
    @Value("${spring.application.name}")
    private String appName;
    @Resource
    private ChatClient chatClient;

    @Override
    public ArticleVo createBlankDoc(Long columnId, long userId) {
        ArticleDo articleDo = new ArticleDo();
        articleDo.setUid(YitIdHelper.nextId())
                .setCreateTime(new Date())
                .setUpdateTime(new Date());
        articleDo.setUserId(userId)
                .setTitle("无标题文档")
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
            articleDo.setTags(JSONUtil.toList(tags, java.util.Map.class));
        }
        articleDo.setUpdateTime(new Date());
        BeanUtil.copyProperties(articleDTO, articleDo);
        // 判断是否需要更新封面文件
        if (!simpleUpdate && articleDTO.getCoverFile() != null) {
            // 上传文件
            ResponseBean<String> upload = storageService
                    .upload(new FileUploadDTO(articleDTO.getCoverFile(), SysConstant.FILE_SCENE_COVER));
            if (!upload.isResult()) {
                log.error("[更新文章信息] --- 封面上传失败，errorInfo: {}", upload.getMessage());
                throw new ServiceException(ServiceErrorCodes.ARTICLE_INFO_UPDATE_FAILED);
            }
            articleDo.setCoverUrl(upload.getData());
        }
        articleDao.updateById(articleDo);
    }

    @Override
    public Map updateContent(BaseRequest<ContentDTO> contentEntity, long userId) {
        ContentDTO contentDTO = contentEntity.getData();
        ArticleDo articleDo = articleDao.selectById(contentDTO.getArticleId());
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
        // 是否需要更新摘要
        if (articleDo.getAutoSummary() == 1) {
            articleDo.setSummary(contentDTO.getSummary());
        }
        // 是否需要更新封面
        if (!StringUtils.hasLength(articleDo.getCoverUrl())) {
            articleDo.setCoverUrl(contentDTO.getCoverUrl());
        }

        articleDo.setUpdateTime(updateTime);
        articleDo.setLatestContentId(uid)
                .setWordsNum(contentDTO.getWordsNum())
                .setPublishStatus(0);
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
        // 判断状态：如果是未审核，则更新为1,如果是审核中则提示无需发布，如果是审核失败，可重新发布
        Integer publishStatus = articleDo.getPublishStatus();
        if (publishStatus != null && publishStatus == 1) {
            return;
        }
        articleDo.setPublishStatus(1);
        articleDao.updateById(articleDo);
        // 发送消息进行内容审核（发布n次，审核n次，这样才能保证内容不会遗漏）
        commonService.sendMessage(appName, publishActionTopic,
                new ContentPublishDTO(articleDo.getUid(),
                        SysConstant.TARGET_TYPE_ARTICLE,
                        articleDo.getLatestContentId(),
                        new Date()));
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
    }

    @Override
    public void articleBatchOperate(BaseRequest<ArticleOperateDTO> requestDto, long userId) {
        ArticleOperateDTO data = requestDto.getData();
        if (SysConstant.ARTICLE_BATCH_MOVE.equals(data.getOperateType())) {
            Assert.notNull(data.getColumnId(), "专栏id不得为空");
        }
        // 获取文章信息判断用于状态判断
        List<ArticleDo> articleList = articleDao.selectBatchIds(data.getArticleList());
        articleList = articleList.stream()
                .filter(article -> CommonStatusEnum.isNormal(article.getStatus())
                        && Objects.equals(article.getUserId(), userId))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(articleList)) {
            throw new ServiceException(ServiceErrorCodes.ARTICLE_NOT_EXIST);
        }
        if (SysConstant.ARTICLE_BATCH_PUBLISH.equals(data.getOperateType())) {
            List<ArticleDo> collect = articleList.stream()
                    .filter(article -> article.getIsPublic() != null && article.getIsPublic() == 1)
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(collect)) {
                throw new ServiceException(ServiceErrorCodes.ARTICLE_PUBLISH_FAILED);
            }
            articleDao.batchUpdate(collect, data.getColumnId(), data.getOperateType());
            return;
        }
        articleDao.batchUpdate(articleList, data.getColumnId(), data.getOperateType());
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
                    .setPublishStatus(0)
                    .setPublishedContentId(null);
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
        for (ContentPublishDTO publishDTO : publishDTOMap.values()) {
            // 获取最新id内容进行审核
            ContentDo contentDo = contentDao.selectById(publishDTO.getContentId());
            if (contentDo == null || !StringUtils.hasLength(contentDo.getContent())) {
                continue;
            }
            try {
                // 对内容进行解析，获取纯文本内容
                JSONObject parseObj = JSONUtil.parseObj(contentDo.getContent());
                String textValue = CommonUtils.getContentTextValue(parseObj);
                log.error("textValue: {}", textValue);
                // 发送文章内容审核请求
                String result = sendChatMessage(textValue);
                JSONObject resultObj = JSONUtil.parseObj(result);
                if (!CollectionUtils.isEmpty(resultObj)) {
                    // 结果解析ok
                    Boolean kimiResult = resultObj.getBool("result");
                    if (kimiResult != null && kimiResult) {
                        log.info("[文章内容审核] --- kimi审核通过");
                        // 存入redis进行文章标签提取
                        String publishKey = RedisKeyConstant.getPublishContentIdKey();
                        String publishId = publishDTO.getTargetId() + SysConstant.SEPARATOR + publishDTO.getContentId();
                        RedisUtil.zset(publishKey, System.currentTimeMillis(), publishId);
                    } else if (kimiResult != null && !kimiResult) {
                        log.info("[文章内容审核] --- kimi审核失败, reason: {}", resultObj.getJSONArray("reason"));
                    }
                }
            } catch (Exception e) {
                log.error("[文章内容审核] --- 正文内容解析失败，contentId: {}", publishDTO.getContentId());
            }
        }
    }

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

    private String sendChatMessage(String textValue) {
        String message = """
                        {
                          "messages": [
                            {"role": "system", "content": "你是文章内容审核管理员，分析内容是否包含违反政治正确、色情淫秽传播等内容，你只需要返回true表示审核通过，false表示审核不通过即可，注意需要联系上下文判断语境是否违规，而不是仅针对单个单词或词语就认定违规，只需要判断出明确违规的内容，不用关心链接或者二维码或者文件什么，不用关心外链是否会存在不合规或非法分享版权的内容，只需要判断链接本身有没有违反包含违反政治正确、包含色情淫秽传播等内容；使用json格式输出。其中result字段表示通过与否，使用true或false；reason字段表示违规内容和原因，指出哪些词语违规了，使用数组表示。"},
                            {"role": "user", "content": "%s"}
                          ],
                          response_format={"type": "json_object"}
                        }
                        """.formatted(textValue);
        String call = this.chatClient.call(message);
        if (log.isDebugEnabled()) {
            log.debug("kimi返回审核结果: {}", call);
        }
        return call;
    }
}
