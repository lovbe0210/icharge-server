package com.lovbe.icharge.controller;

import com.lovbe.icharge.common.exception.GlobalErrorCodes;
import com.lovbe.icharge.common.exception.ServiceException;
import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.entity.dto.FavoriteTagsDTO;
import com.lovbe.icharge.entity.dto.FavoriteTargetDTO;
import com.lovbe.icharge.entity.vo.PublicArticleVo;
import com.lovbe.icharge.service.FavoriteService;
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
 * @Date: 2024/11/25 23:13
 * @Description: 收藏夹
 */
@RestController
public class FavoriteController {
    @Resource
    private FavoriteService favoritesService;

    /**
     * description: 获取收藏夹分类标签
     *
     * @param userId
     * @return ResponseBean<ArticleVO>
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     */
    @PostMapping("/favorite/tags/list")
    public ResponseBean<PublicArticleVo> getFavoriteTagList(@RequestHeader("userId") Long userId) {
        List<FavoriteTagsDTO> data = favoritesService.getFavoriteTagList(userId);
        return ResponseBean.ok(data);
    }

    /**
     * description: 添加/编辑 收藏夹分类标签
     *
     * @param userId
     * @return ResponseBean<ArticleVO>
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     */
    @PostMapping("/favorite/tags/edit")
    public ResponseBean<PublicArticleVo> updateFavoriteTag(@RequestBody @Valid BaseRequest<FavoriteTagsDTO> baseRequest,
                                                           @RequestHeader("userId") Long userId) {
        Assert.notNull(baseRequest.getData().getTitle(), "收藏分组标签不得为空");
        favoritesService.updateFavoriteTag(baseRequest.getData(), userId);
        return ResponseBean.ok();
    }

    /**
     * description: 删除 收藏夹分类标签
     *
     * @param userId
     * @return ResponseBean<ArticleVO>
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     */
    @PostMapping("/favorite/tags/delete")
    public ResponseBean<PublicArticleVo> deleteFavoriteTag(@RequestBody @Valid BaseRequest<FavoriteTagsDTO> baseRequest,
                                                           @RequestHeader("userId") Long userId) {
        Assert.notNull(baseRequest.getData().getUid(), "收藏分组标签id不得为空");
        favoritesService.deleteFavoriteTag(baseRequest.getData(), userId);
        return ResponseBean.ok();
    }

    /**
     * description: 收藏标记
     *
     * @param userId
     * @return ResponseBean<ArticleVO>
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     */
    @PostMapping("/mine/marks")
    public ResponseBean<PublicArticleVo> marksContent(@RequestBody @Valid BaseRequest<FavoriteTargetDTO> baseRequest,
                                                      @RequestHeader("userId") Long userId) {
        FavoriteTargetDTO data = baseRequest.getData();
        if (data.getTargetId() == null || data.getTargetType() == null) {
            throw new ServiceException(GlobalErrorCodes.BAD_REQUEST);
        }
        favoritesService.marksContent(data, userId);
        return ResponseBean.ok();
    }

    /**
     * description: 取消收藏
     *
     * @param userId
     * @return ResponseBean<ArticleVO>
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     */
    @PostMapping("/mine/unmark")
    public ResponseBean<PublicArticleVo> cancelMarkContent(@RequestBody @Valid BaseRequest<FavoriteTargetDTO> baseRequest,
                                                           @RequestHeader("userId") Long userId) {
        FavoriteTargetDTO data = baseRequest.getData();
        if (data.getFtId() == null) {
            throw new ServiceException(GlobalErrorCodes.BAD_REQUEST);
        }
        favoritesService.cancelMarkContent(data.getFtId(), userId);
        return ResponseBean.ok();
    }
}
