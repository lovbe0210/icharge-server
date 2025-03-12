package com.lovbe.icharge.controller;

import com.lovbe.icharge.common.enums.SysConstant;
import com.lovbe.icharge.common.exception.ServiceErrorCodes;
import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.common.model.base.PageBean;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.common.model.dto.RequestListDTO;
import com.lovbe.icharge.common.model.vo.PublicArticleVo;
import com.lovbe.icharge.common.model.vo.RamblyJotVo;
import com.lovbe.icharge.entity.dto.RecommendRequestDTO;
import com.lovbe.icharge.entity.vo.*;
import com.lovbe.icharge.service.PublicContentService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
                                                        @RequestHeader(value = SysConstant.USERID, required = false) Long userId) {
        return ResponseBean.ok(contentService.getArticleInfo(uri, userId));
    }

    /**
     * description: 通过id批量获取文章列表
     *
     * @param userId
     * @return ResponseBean<ArticleVO>
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     */
    @PostMapping("/article/ids")
    public ResponseBean<List<PublicArticleVo>> getArticleListByIds(@RequestBody BaseRequest<List<Long>> baseRequest,
                                                             @RequestHeader(value = SysConstant.USERID) Long userId) {
        return ResponseBean.ok(contentService.getArticleListByIds(baseRequest.getData(), userId));
    }

    /**
     * @description: 获取公开随笔列表
     * @param: baseRequest
     * @param: userId
     * @return: com.lovbe.icharge.common.model.base.ResponseBean<com.lovbe.icharge.common.model.base.PageBean>
     * @author: lovbe0210
     * @date: 2025/1/26 23:25
     */
    @PostMapping("/ramblyjot/list")
    public ResponseBean<PageBean> getPublicRamblyJotList(@RequestBody @Valid BaseRequest<RequestListDTO> baseRequest,
                                                         @RequestHeader(value = SysConstant.USERID, required = false) Long userId){
        return ResponseBean.ok(contentService.getPublicRamblyJot(baseRequest, userId));
    }

    /**
     * description: 获取随笔信息用于阅读
     *
     * @param userId
     * @return ResponseBean<RamblyJotVo>
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     */
    @GetMapping("/ramblyjot/{ramblyJotId}")
    public ResponseBean<RamblyJotVo> getRamblyJotInfo(@PathVariable("ramblyJotId") Long ramblyJotId,
                                                      @RequestHeader(value = SysConstant.USERID, required = false) Long userId) {
        return ResponseBean.ok(contentService.getRamblyJotInfo(userId, ramblyJotId));
    }

    /**
     * description: 通过id批量获取随笔列表
     *
     * @param userId
     * @return ResponseBean<ArticleVO>
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     */
    @PostMapping("/ramblyjot/ids")
    public ResponseBean<List<RamblyJotVo>> getRamblyjotListByIds(@RequestBody BaseRequest<List<Long>> baseRequest,
                                                                 @RequestHeader(value = SysConstant.USERID) Long userId) {
        return ResponseBean.ok(contentService.getRamblyjotListByIds(baseRequest.getData(), userId));
    }

    /**
     * description: 上报阅读进度
     * @param st
     * @param csh
     * @param sh
     * @param sign
     */
    @GetMapping("/article/view")
    public void reportView(@RequestParam("st") Double st,
                           @RequestParam("csh") Double csh,
                           @RequestParam ("sh")Double sh,
                           @RequestHeader("sign") String sign,
                           @RequestHeader(value = SysConstant.USERID, required = false) Long userId) {
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
                                                        @RequestHeader(value = SysConstant.USERID, required = false) Long userId) {
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
                                                      @RequestHeader(value = SysConstant.USERID, required = false) Long userId) {
        return ResponseBean.ok(contentService.getColumnDir(uri, userId));
    }

    /**
     * description: 获取分类文章列表
     *
     * @param userId
     * @return ResponseBean<ArticleVO>
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     */
    @PostMapping("/article/category")
    public ResponseBean<Map> getCategoryArticleList(@RequestBody @Valid BaseRequest<RecommendRequestDTO> baseRequest,
                                                    @RequestHeader(value = SysConstant.USERID, required = false) Long userId) {
        RecommendRequestDTO data = baseRequest.getData();
        Assert.notNull(data.getFirstCategory(), ServiceErrorCodes.CATE_MENU_NOT_NULL.getMsg());
        PageBean<FeaturedArticleVo> recommendArticle = contentService.getCategoryArticleList(baseRequest, userId);
        return ResponseBean.ok(recommendArticle);
    }

    /**
     * description: 获取推荐文章列表
     *
     * @param userId
     * @return ResponseBean<ArticleVO>
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     */
    @PostMapping("/article/recommend")
    public ResponseBean<Map> getRecommendedArticleList(@RequestBody @Valid BaseRequest<RecommendRequestDTO> baseRequest,
                                                       @RequestHeader(value = SysConstant.USERID, required = false) Long userId) {
        PageBean<FeaturedArticleVo> recommendArticle = contentService.getRecommendedArticleList(baseRequest, userId);
        return ResponseBean.ok(recommendArticle);
    }

    /**
     * description: 获取首页精选文章(排行榜前3）
     *
     * @return ResponseBean<ArticleVO>
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     */
    @GetMapping("/article/featured")
    public ResponseBean getFeaturedArticle() {
        List<FeaturedArticleVo> featuredArticle = contentService.getFeaturedArticle();
        return ResponseBean.ok(featuredArticle);
    }

    /**
     * description: 获取精选文章排行榜
     *
     * @return ResponseBean<ArticleVO>
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     */
    @PostMapping("/article/rank")
    public ResponseBean getRankArticle(@RequestBody @Valid BaseRequest<RecommendRequestDTO> baseRequest,
                                       @RequestHeader(value = SysConstant.USERID, required = false) Long userId) {
        PageBean<FeaturedArticleVo> rankPageBean = contentService.getRankArticleList(baseRequest.getData(), userId);
        return ResponseBean.ok(rankPageBean);
    }

    /**
     * description: 获取首页精选专栏(排行榜前3）
     *
     * @return ResponseBean<ArticleVO>
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     */
    @GetMapping("/column/featured")
    public ResponseBean getFeaturedColumn() {
        List<RecommendColumnVo> featuredArticle = contentService.getFeaturedColumn();
        return ResponseBean.ok(featuredArticle);
    }

    /**
     * description: 获取首页精选专栏
     *
     * @return ResponseBean<ArticleVO>
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     */
    @PostMapping("/column/rank")
    public ResponseBean getRankColumn(@RequestBody @Valid BaseRequest<RecommendRequestDTO> baseRequest,
                                      @RequestHeader(value = SysConstant.USERID, required = false) Long userId) {
        PageBean<RecommendColumnVo> featuredArticle = contentService.getRankColumn(baseRequest.getData(), userId);
        return ResponseBean.ok(featuredArticle);
    }

    /**
     * @description: 获取个人主页公开专栏
     * @param: baseRequest
     * @return: com.lovbe.icharge.common.model.base.ResponseBean
     * @author: lovbe0210
     * @date: 2025/2/7 22:22
     */
    @PostMapping("/column/domain")
    public ResponseBean getDomainPublicColumn(@RequestBody @Valid BaseRequest<RecommendRequestDTO> baseRequest) {
        RecommendRequestDTO data = baseRequest.getData();
        Assert.notNull(data.getUserId(), "用户id不得为空");
        List<RecommendColumnVo> publicColumnList = contentService.getDomainPublicColumn(data);
        return ResponseBean.ok(publicColumnList);
    }

    /**
     * description: 获取首页优秀创作者(排行榜前3）
     *
     * @return ResponseBean<ArticleVO>
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     */
    @GetMapping("/author/excellent")
    public ResponseBean getExcellentAuthor() {
        List<ExcellentAuthorVo> excellentAuthor = contentService.getExcellentAuthor();
        return ResponseBean.ok(excellentAuthor);
    }

    /**
     * description: 获取创作者排行榜
     *
     * @return ResponseBean<ArticleVO>
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     */
    @PostMapping("/author/rank")
    public ResponseBean getRankAuthor(@RequestBody @Valid BaseRequest<RecommendRequestDTO> baseRequest,
                                      @RequestHeader(value = SysConstant.USERID, required = false) Long userId) {
        PageBean<ExcellentAuthorVo> rankAuthor = contentService.getRankAuthor(baseRequest.getData(), userId);
        return ResponseBean.ok(rankAuthor);
    }

    /**
     * description: 获取首页公开专栏
     *
     * @return ResponseBean<ArticleVO>
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     */
    @PostMapping("/column/public")
    public ResponseBean getPublicColumn(@RequestBody @Valid BaseRequest<RecommendRequestDTO> baseRequest,
                                        @RequestHeader(value = SysConstant.USERID, required = false) Long userId) {
        PageBean<RecommendColumnVo> featuredArticle = contentService.getPublicColumn(baseRequest.getData(), userId);
        return ResponseBean.ok(featuredArticle);
    }

    /**
     * description: 获取关注用户动态
     *
     * @return ResponseBean<ArticleVO>
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     */
    @PostMapping("/follow/create/record")
    public ResponseBean getCreateRecord(@RequestBody @Valid BaseRequest<RecommendRequestDTO> baseRequest,
                                        @RequestHeader(value = SysConstant.USERID) Long userId) {
        PageBean<CreateRecordVo> featuredArticle = contentService.getCreateRecord(baseRequest.getData(), userId);
        return ResponseBean.ok(featuredArticle);
    }
}
