package com.lovbe.icharge.controller;

import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.entity.PublicArticleVo;
import com.lovbe.icharge.entity.RouterInfoVo;
import com.lovbe.icharge.service.PublicContentService;
import jakarta.annotation.Resource;
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
     * description: 根据uri判断为1文章还是2专栏
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     * @return ResponseBean<LoginUser>
     */
    @GetMapping("/router/{dynamicId}")
    public ResponseBean<RouterInfoVo> getRouterType(@PathVariable("dynamicId") String dynamicId) {
        return ResponseBean.ok(contentService.getRouterDirection(dynamicId));
    }

    /**
     * description: 获取文档信息用于阅读
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

    /**
     * description: 获取文档信息用于阅读
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     * @param userId
     * @return ResponseBean<ArticleVO>
     */
    @GetMapping("/article/list/{domain}")
    public ResponseBean<PublicArticleVo> getArticleList(@PathVariable("domain") String domain,
                                                        @RequestHeader(value = "userId", required = false) String userId) {
        return ResponseBean.ok(contentService.getArticleList(domain, userId));
    }
}
