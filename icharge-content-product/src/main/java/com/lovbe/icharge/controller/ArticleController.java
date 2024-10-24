package com.lovbe.icharge.controller;

import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.common.model.dto.RequestListDTO;
import com.lovbe.icharge.entity.dto.ArticleDTO;
import com.lovbe.icharge.entity.dto.ContentDTO;
import com.lovbe.icharge.entity.vo.ArticleVO;
import com.lovbe.icharge.entity.vo.ContentVO;
import com.lovbe.icharge.service.ArticleService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @Author: lovbe0210
 * @Date: 2024/10/9 23:46
 * @Description: 文档相关controller
 */
@RestController
public class ArticleController {
    @Resource
    private ArticleService articleService;

    /**
     * description: 创建空白文档
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     * @param userId
     * @return ResponseBean<LoginUser>
     */
    @PostMapping("/createBlankDoc")
    public ResponseBean<ArticleVO> createBlankDoc(@RequestHeader("userId") long userId) {
        return ResponseBean.ok(articleService.createBlankDoc(userId));
    }

    /**
     * description: 获取文档信息用于编辑
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     * @param userId
     * @return ResponseBean<LoginUser>
     */
    @PostMapping("/article/{articleId}")
    public ResponseBean<ArticleVO> getArticleForEdit(@PathVariable("articleId") String articleId,
                                                     @RequestHeader("userId") long userId) {
        return ResponseBean.ok(articleService.getArticleForEdit(articleId, userId));
    }

    /**
     * description: 更新文档信息
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     * @param articleEntity
     * @param userId
     * @return ResponseBean<LoginUser>
     */
    @PostMapping("/article/update")
    public ResponseBean<ArticleVO> updateArticle(@RequestBody @Valid BaseRequest<ArticleDTO> articleEntity,
                                                 @RequestHeader("userId") long userId) {
        articleService.updateArticle(articleEntity, userId);
        return ResponseBean.ok();
    }

    /**
     * description: 获取文档内容
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     * @param articleId
     * @param userId
     * @return ResponseBean<LoginUser>
     */
    @PostMapping("/content/{articleId}")
    public ResponseBean<ArticleVO> getContent(@PathVariable("articleId") Long articleId,
                                              @RequestHeader("userId") long userId) {
        ContentVO content = articleService.getContent(articleId, userId);
        return ResponseBean.ok(content);
    }

    /**
     * description: 更新文档内容
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     * @param contentEntity
     * @param userId
     * @return ResponseBean<LoginUser>
     */
    @PostMapping("/article/content")
    public ResponseBean<ArticleVO> updateContent(@RequestBody @Valid BaseRequest<ContentDTO> contentEntity,
                                                 @RequestHeader("userId") long userId) {
        Map content = articleService.updateContent(contentEntity, userId);
        return ResponseBean.ok(content);
    }

    /**
     * description: 获取文档列表
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     * @param requestDto
     * @param userId
     * @return ResponseBean<LoginUser>
     */
    @PostMapping("/articleList")
    public ResponseBean<ArticleVO> getMyArticleList(@RequestBody @Valid BaseRequest<RequestListDTO> requestDto,
                                                 @RequestHeader("userId") long userId) {
        List<ArticleVO> articleList = articleService.getMyArticleList(requestDto, userId);
        return ResponseBean.ok(articleList);
    }
}
