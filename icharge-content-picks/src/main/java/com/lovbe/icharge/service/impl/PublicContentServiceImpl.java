package com.lovbe.icharge.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.lovbe.icharge.common.exception.ServiceErrorCodes;
import com.lovbe.icharge.common.exception.ServiceException;
import com.lovbe.icharge.common.model.dto.ArticleDo;
import com.lovbe.icharge.common.model.dto.ContentDo;
import com.lovbe.icharge.dao.PublicContentDao;
import com.lovbe.icharge.entity.PublicArticleVo;
import com.lovbe.icharge.service.PublicContentService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @Author: lovbe0210
 * @Date: 2024/11/19 0:32
 * @Description: MS
 */
@Service
public class PublicContentServiceImpl implements PublicContentService {
    @Resource
    private PublicContentDao publicContentDao;
    @Override
    public PublicArticleVo getArticleInfo(String articleUri, String userId) {
        // 如果userId和文档所在userId相同，则使用最新的contentId，否则使用已发布的contentId
        ArticleDo articleDo = publicContentDao.selectArticleInfo(articleUri);
        if (articleDo == null) {
            throw new ServiceException(ServiceErrorCodes.ARTICLE_NOT_EXIST);
        }
        PublicArticleVo articleVo = new PublicArticleVo();
        BeanUtil.copyProperties(articleDo, articleVo);
        if (Objects.equals(String.valueOf(articleDo.getUserId()), userId)) {
            // 作者本人，取最新内容版本id
            Long latestContentId = articleDo.getLatestContentId();
            if (latestContentId != null) {
                ContentDo contentDo = publicContentDao.selectContent(latestContentId);
                if (contentDo != null) {
                    articleVo.setContent(contentDo.getContent());
                }
            }
            return articleVo;
        }
        // 非作者阅读或作者未登录阅读
        if (articleDo.getIsPublic() == 0 || articleDo.getPublishedContentId() == null) {
            throw new ServiceException(ServiceErrorCodes.ARTICLE_NOT_EXIST);
        }
        ContentDo contentDo = publicContentDao.selectContent(articleDo.getPublishedContentId());
        if (contentDo == null) {
            return articleVo;
        }
        articleVo.setContent(contentDo.getContent());
        return articleVo;
    }
}
