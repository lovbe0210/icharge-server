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
    @GetMapping("/article/{uri}")
    public ResponseBean<PublicArticleVo> getArticleInfo(@PathVariable("uri") String uri,
                                                        @RequestHeader(value = "userId", required = false) Long userId) {
        return ResponseBean.ok(contentService.getArticleInfo(uri, userId));
    }

    /**
     * description: 获取作者公开文档列表
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     * @param userId
     * @return ResponseBean<ArticleVO>
     */
    @GetMapping("/article/list/{authorId}")
    public ResponseBean<PublicArticleVo> getArticleList(@PathVariable("authorId") Long authorId,
                                                        @RequestHeader(value = "userId", required = false) Long userId) {
        return ResponseBean.ok(contentService.getArticleList(authorId, userId));
    }

    /**
     * description: 获取公开专栏文章目录
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     * @param userId
     * @return ResponseBean<ArticleVO>
     */
    @GetMapping("/column/dir/{uri}")
    public ResponseBean<PublicArticleVo> getColumnDir(@PathVariable("uri") String uri,
                                                      @RequestHeader(value = "userId", required = false) Long userId) {
        return ResponseBean.ok(contentService.getColumnDir(uri, userId));
    }

}
