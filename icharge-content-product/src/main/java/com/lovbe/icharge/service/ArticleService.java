package com.lovbe.icharge.service;

import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.entity.dto.ArticleDTO;
import com.lovbe.icharge.entity.dto.ContentDTO;
import com.lovbe.icharge.entity.vo.ArticleVO;

import java.util.Map;

/**
 * @Author: lovbe0210
 * @Date: 2024/10/9 23:53
 * @Description: 文档相关service
 */
public interface ArticleService {
    /**
     * @description 创建空白文档
     * @param[1] userId
     * @return ArticleVO
     * @author lovbe0210
     * @date 2024/10/20 16:02
     */
    ArticleVO createBlankDoc(long userId);

    /**
     * @description 获取文档信息用于编辑
     * @param[1] articleId
     * @param[2] userId
     * @return ArticleVO
     * @author lovbe0210
     * @date 2024/10/20 16:02
     */
    ArticleVO getArticleForEdit(String articleId, long userId);

    /**
     * @description 更新文档信息
     * @param[1] articleEntity
     * @param[2] userId
     * @author lovbe0210
     * @date 2024/10/20 16:03
     */
    void updateArticle(BaseRequest<ArticleDTO> articleEntity, long userId);

    /**
     * @return long
     * @description 文档内容更新
     * @param[1] contentEntity
     * @author lovbe0210
     * @date 2024/10/20 16:38
     */
    Map updateContent(BaseRequest<ContentDTO> contentEntity, long userId);
}
