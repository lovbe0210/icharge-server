package com.lovbe.icharge.controller;

import com.lovbe.icharge.common.enums.SysConstant;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.entity.PublicArticleVo;
import com.lovbe.icharge.service.PublicContentService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: lovbe0210
 * @Date: 2024/11/18 23:43
 * @Description: 公开访问内容请求
 */
@RestController
public class PublicContentController {
    @Resource
    private PublicContentService contentService;
    /**
     * description: 获取文档信息用于编辑
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     * @param userId
     * @return ResponseBean<ArticleVO>
     */
    @GetMapping("/article/{articleUri}")
    public ResponseBean<PublicArticleVo> getArticleInfo(@PathVariable("articleUri") String articleUri,
                                                        @RequestHeader(value = "userId", required = false) String userId) {
        return ResponseBean.ok(contentService.getArticleInfo(articleUri, userId));
    }
}
