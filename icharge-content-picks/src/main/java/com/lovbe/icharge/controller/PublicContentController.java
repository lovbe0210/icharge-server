package com.lovbe.icharge.controller;

import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.entity.dto.RecommendRequestDTO;
import com.lovbe.icharge.entity.vo.PublicArticleVo;
import com.lovbe.icharge.entity.vo.RecommendArticleVo;
import com.lovbe.icharge.entity.vo.RouterInfoVo;
import com.lovbe.icharge.service.PublicContentService;
import jakarta.annotation.Resource;
import org.springframework.data.repository.query.Param;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

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
     *
     * @return ResponseBean<LoginUser>
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     */
    @GetMapping("/router/{dynamicId}")
    public ResponseBean<RouterInfoVo> getRouterType(@PathVariable("dynamicId") String dynamicId) {
        return ResponseBean.ok(contentService.getRouterDirection(dynamicId));
    }

    /**
     * description: 获取文档信息用于阅读
     *
     * @param userId
     * @return ResponseBean<ArticleVO>
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     */
    @GetMapping("/article/{uri}")
    public ResponseBean<PublicArticleVo> getArticleInfo(@PathVariable("uri") String uri,
                                                        @RequestHeader(value = "userId", required = false) Long userId) {
        return ResponseBean.ok(contentService.getArticleInfo(uri, userId));
    }

    /**
     * description: 上报阅读进度
     * @param st
     * @param csh
     * @param sh
     * @param sign
     */
    @GetMapping("/ae/view")
    public void reportView(@RequestParam("st") Double st,
                           @RequestParam("csh") Double csh,
                           @RequestParam ("sh")Double sh,
                           @RequestHeader("sign") String sign,
                           @RequestHeader(value = "userId", required = false) Long userId) {
        if (st == null || csh == null || sh == null || !StringUtils.hasLength(sign)) {
            return;
        }
        contentService.reportArticleView(st, csh, sh, sign, userId);
    }

    /**
     * description: 获取作者公开文档列表
     *
     * @param userId
     * @return ResponseBean<ArticleVO>
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     */
    @GetMapping("/article/list/{authorId}")
    public ResponseBean<PublicArticleVo> getArticleList(@PathVariable("authorId") Long authorId,
                                                        @RequestHeader(value = "userId", required = false) Long userId) {
        return ResponseBean.ok(contentService.getArticleList(authorId, userId));
    }

    /**
     * description: 获取公开专栏文章目录
     *
     * @param userId
     * @return ResponseBean<ArticleVO>
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     */
    @GetMapping("/column/dir/{uri}")
    public ResponseBean<PublicArticleVo> getColumnDir(@PathVariable("uri") String uri,
                                                      @RequestHeader(value = "userId", required = false) Long userId) {
        return ResponseBean.ok(contentService.getColumnDir(uri, userId));
    }

    /**
     * description: 获取推荐文章列表
     *
     * @param userId
     * @return ResponseBean<ArticleVO>
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     */
    @GetMapping("/article/recommended")
    public ResponseBean<RecommendArticleVo> getRecommendedArticleList(@RequestBody BaseRequest<RecommendRequestDTO> baseRequest,
                                                                      @RequestHeader(value = "userId", required = false) Long userId) {
        return ResponseBean.ok(contentService.getRecommendedArticleList(baseRequest, userId));
    }

}
