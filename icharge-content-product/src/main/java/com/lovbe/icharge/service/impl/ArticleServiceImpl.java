package com.lovbe.icharge.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONUtil;
import com.github.yitter.idgen.YitIdHelper;
import com.lovbe.icharge.common.enums.CommonStatusEnum;
import com.lovbe.icharge.common.enums.SysConstant;
import com.lovbe.icharge.common.exception.GlobalErrorCodes;
import com.lovbe.icharge.common.exception.ServiceErrorCodes;
import com.lovbe.icharge.common.exception.ServiceException;
import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.entity.dto.ArticleDTO;
import com.lovbe.icharge.entity.dto.ArticleDo;
import com.lovbe.icharge.entity.dto.ContentDTO;
import com.lovbe.icharge.entity.dto.ContentDo;
import com.lovbe.icharge.entity.vo.ArticleVO;
import com.lovbe.icharge.mapper.ArticleMapper;
import com.lovbe.icharge.mapper.ColumnMapper;
import com.lovbe.icharge.mapper.ContentMapper;
import com.lovbe.icharge.service.ArticleService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: lovbe0210
 * @Date: 2024/10/9 23:54
 * @Description: MS
 */
@Service
public class ArticleServiceImpl implements ArticleService {
    @Resource
    private ArticleMapper articleMapper;
    @Resource
    private ContentMapper contentMapper;
    @Resource
    private ColumnMapper columnMapper;

    @Override
    public ArticleVO createBlankDoc(long userId) {
        ArticleDo articleDo = new ArticleDo();
        articleDo.setUid(YitIdHelper.nextId())
                .setCreateTime(new Date())
                .setUpdateTime(new Date());
        articleDo.setUserId(userId).setTitle("无标题文档");
        articleMapper.insertOrUpdate(articleDo);
        ArticleVO articleVO = new ArticleVO();
        BeanUtil.copyProperties(articleDo, articleVO);
        return articleVO;
    }

    @Override
    public ArticleVO getArticleForEdit(String articleId, long userId) {
        ArticleVO articlevo = articleMapper.selectArticleForEdit(userId, articleId);
        if (articlevo == null || !CommonStatusEnum.isNormal(articlevo.getStatus())) {
            throw new ServiceException(ServiceErrorCodes.ARTICLE_NOT_EXIST);
        }
        if (CommonStatusEnum.isDisable(articlevo.getStatus())) {
            throw new ServiceException(ServiceErrorCodes.ARTICLE_STATUS_ERROR);
        }
        return articlevo;
    }

    @Override
    public void updateArticle(BaseRequest<ArticleDTO> articleEntity, long userId) {
        ArticleDTO articleDTO = articleEntity.getData();
        ArticleDo articleDo = articleMapper.selectById(articleDTO.getUid());
        checkArticleStatus(userId, articleDo);
        String tags = articleDTO.getTagsArray();
        if (StringUtils.hasLength(tags)) {
            articleDo.setTags(JSONUtil.toList(tags, java.util.Map.class));
        }
        articleDo.setUpdateTime(new Date());
        BeanUtil.copyProperties(articleDTO, articleDo);
        articleMapper.updateById(articleDo);
    }

    @Override
    public Map updateContent(BaseRequest<ContentDTO> contentEntity, long userId) {
        ContentDTO contentDTO = contentEntity.getData();
        ArticleDo articleDo = articleMapper.selectById(contentDTO.getArticleId());
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
        contentMapper.insertOrUpdate(contentDo);

        // 判断是否需要更新文档信息
        if (articleDo.getWordsNum() == 0 && contentDTO.getWordsNum() == 0) {
            map.put(SysConstant.CONTENT_ID, uid);
            return map;
        }
        articleDo.setUpdateTime(updateTime);
        articleDo.setLatestContentId(uid)
                .setWordsNum(contentDTO.getWordsNum())
                .setPublishStatus(0);
        articleMapper.updateById(articleDo);
        map.put(SysConstant.CONTENT_ID, uid);
        return map;
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
}
