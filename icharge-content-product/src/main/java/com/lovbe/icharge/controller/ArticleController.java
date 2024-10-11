package com.lovbe.icharge.controller;

import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.entity.vo.ArticleVO;
import com.lovbe.icharge.service.ArticleService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

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
    @PostMapping("/articleInfo/{articleId}")
    public ResponseBean<ArticleVO> getArticleForEdit(@PathVariable("articleId") String articleId, @RequestHeader("userId") long userId) {
        return ResponseBean.ok(articleService.getArticleForEdit(articleId, userId));
    }
}
