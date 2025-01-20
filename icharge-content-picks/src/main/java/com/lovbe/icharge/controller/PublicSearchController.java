package com.lovbe.icharge.controller;

import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.entity.dto.GlobalSearchDTO;
import com.lovbe.icharge.entity.vo.ExcellentUserVo;
import com.lovbe.icharge.entity.vo.FeaturedArticleVo;
import com.lovbe.icharge.entity.vo.RecommendColumnVo;
import com.lovbe.icharge.entity.vo.SearchResultVo;
import com.lovbe.icharge.service.PublicSearchService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author: lovbe0210
 * @Date: 2025/1/20 15:13
 * @Description: 公开内容搜索相关接口
 */
@RestController
public class PublicSearchController {
    @Resource
    private PublicSearchService searchService;

    /**
     * description: 全局搜索
     *
     * @return ResponseBean<ArticleVO>
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     */
    @PostMapping("/search/global")
    public ResponseBean getGlobalSearchResult(@RequestBody @Valid BaseRequest<GlobalSearchDTO> baseRequest,
                                              @RequestHeader(value = "userId", required = false) Long userId) {
        SearchResultVo searchResult = searchService.getGlobalSearchResult(baseRequest.getData(), userId);
        return ResponseBean.ok(searchResult);
    }

    /**
     * description: 全局搜索
     *
     * @return ResponseBean<ArticleVO>
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     */
    @PostMapping("/search/global/user")
    public ResponseBean getGlobalSearchUserList(@RequestBody @Valid BaseRequest<GlobalSearchDTO> baseRequest,
                                                @RequestHeader(value = "userId", required = false) Long userId) {
        List<ExcellentUserVo> searchUserList = searchService.getGlobalSearchUserList(baseRequest.getData(), userId);
        return ResponseBean.ok(searchUserList);
    }

    /**
     * description: 全局搜索专栏
     *
     * @return ResponseBean<ArticleVO>
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     */
    @PostMapping("/search/global/column")
    public ResponseBean getGlobalSearchColumnList(@RequestBody @Valid BaseRequest<GlobalSearchDTO> baseRequest,
                                                  @RequestHeader(value = "userId", required = false) Long userId) {
        List<RecommendColumnVo> searchcolumnList = searchService.getGlobalSearchColumnList(baseRequest.getData(), userId);
        return ResponseBean.ok(searchcolumnList);
    }

    /**
     * description: 全局搜索文章
     *
     * @return ResponseBean<ArticleVO>
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     */
    @PostMapping("/search/global/article")
    public ResponseBean getGlobalSearchArticleList(@RequestBody @Valid BaseRequest<GlobalSearchDTO> baseRequest,
                                                   @RequestHeader(value = "userId", required = false) Long userId) {
        List<FeaturedArticleVo> searchArticleList = searchService.getGlobalSearchArticleList(baseRequest.getData(), userId);
        return ResponseBean.ok(searchArticleList);
    }

    /**
     * description: 范围搜索
     *
     * @return ResponseBean<ArticleVO>
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     */
    @PostMapping("/search/scope")
    public ResponseBean getScopeSearchResult(@RequestBody @Valid BaseRequest<GlobalSearchDTO> baseRequest,
                                             @RequestHeader(value = "userId", required = false) Long userId) {
        GlobalSearchDTO data = baseRequest.getData();
        Assert.isTrue(data.getColumnId() != null || data.getUserId() != null, "搜索范围不得为空");
        List<FeaturedArticleVo> searchArticleList = searchService.getScopeSearchResult(data, userId);
        return ResponseBean.ok(searchArticleList);
    }
}
