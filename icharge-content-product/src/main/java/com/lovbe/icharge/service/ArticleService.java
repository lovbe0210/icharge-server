package com.lovbe.icharge.service;

import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.common.model.dto.ArticleDo;
import com.lovbe.icharge.common.model.dto.ContentDo;
import com.lovbe.icharge.common.model.dto.RequestListDTO;
import com.lovbe.icharge.entity.dto.ArticleDTO;
import com.lovbe.icharge.entity.dto.ArticleOperateDTO;
import com.lovbe.icharge.entity.dto.ContentDTO;
import com.lovbe.icharge.common.model.dto.ContentPublishDTO;
import com.lovbe.icharge.entity.vo.ArticleVo;
import com.lovbe.icharge.entity.vo.ContentVo;

import java.util.List;
import java.util.Map;

/**
 * @Author: lovbe0210
 * @Date: 2024/10/9 23:53
 * @Description: 文档相关service
 */
public interface ArticleService {
    /**
     * @description 创建空白文档
     * @param[1] columnId
     * @param[1] userId
     * @return ArticleVO
     * @author lovbe0210
     * @date 2024/10/20 16:02
     */
    ArticleVo createBlankDoc(Long columnId, long userId);

    /**
     * @description 获取文档信息用于编辑
     * @param[1] articleId
     * @param[2] userId
     * @return ArticleVO
     * @author lovbe0210
     * @date 2024/10/20 16:02
     */
    ArticleVo getArticleForEdit(String uri, long userId);

    /**
     * @description 更新文档信息
     * @param[1] articleDTO
     * @param[2] userId
     * @author lovbe0210
     * @date 2024/10/20 16:03
     */
    void updateArticle(boolean simpleUpdate, ArticleDTO articleDTO, long userId);

    /**
     * @return long
     * @description 文档内容更新
     * @param[1] contentEntity
     * @author lovbe0210
     * @date 2024/10/20 16:38
     */
    Map updateContent(BaseRequest<ContentDTO> contentEntity, long userId);

    /**
     * @description 获取我的文档列表
     * @param[1] requestDto
     * @param[2] userId
     * @return List<ArticleVO>
     * @author lovbe0210
     * @date 2024/10/21 22:00
     */
    List<ArticleVo> getMyArticleList(BaseRequest<RequestListDTO> requestDto, long userId);

    /**
     * @description 获取文档内容
     * @param[1] articleId
     * @param[2] userId
     * @return ContentVO
     * @author lovbe0210
     * @date 2024/10/24 17:39
     */
    ContentVo getContent(Long articleId, long userId);

    /**
     * @description 文章置顶/取消置顶
     * @param[1] requestDto
     * @param[2] userId
     * @author lovbe0210
     * @date 2024/10/27 19:52
     */
    void updateArticleTop(BaseRequest<ArticleDTO> requestDto, long userId);

    /**
     * @description 文章发布
     * @param[1] articleId
     * @param[2] userId
     * @author lovbe0210
     * @date 2024/10/29 0:05
     */
    void publishArticle(Long articleId, long userId);

    /**
     * @description 文章删除
     * @param[1] articleId
     * @param[2] userId
     * @author lovbe0210
     * @date 2024/10/29 0:27
     */
    void deleteArticle(Long articleId, long userId);

    void articleBatchOperate(BaseRequest<ArticleOperateDTO> requestDto, long userId);

    /**
     * @description 批量发布文章
     * @param[1] articleId
     * @param[2] userId
     * @author lovbe0210
     * @date 2024/10/29 0:27
     */
    void batchPublish(List<ArticleDo> articleList);

    /**
     * @description 专栏文章复制
     * @param[1] data
     * @param[2] userId
     * @return Map<ArticleVO>
     * @author lovbe0210
     * @date 2024/11/13 23:23
     */
    Map<Long, ArticleVo> copyArticle(ArticleOperateDTO data, long userId);

    /**
     * 内容发布处理
     * @param collect
     */
    void handlerPublishAction(List<ContentPublishDTO> collect);

    /**
     * 文章审核状态更新
     * @param publishDTO
     * @param contentDo
     */
    void updateArticlePublishStatus(ContentPublishDTO publishDTO, ContentDo contentDo);
}
