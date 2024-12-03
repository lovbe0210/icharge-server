package com.lovbe.icharge.controller;

import com.lovbe.icharge.common.exception.GlobalErrorCodes;
import com.lovbe.icharge.common.exception.ServiceException;
import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.entity.dto.CollectRequestDTO;
import com.lovbe.icharge.entity.dto.CollectTagsDTO;
import com.lovbe.icharge.entity.dto.CollectTargetDTO;
import com.lovbe.icharge.entity.vo.CollectTagsVo;
import com.lovbe.icharge.entity.vo.CollectVo;
import com.lovbe.icharge.entity.vo.PublicArticleVo;
import com.lovbe.icharge.service.CollectService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: lovbe0210
 * @Date: 2024/11/25 23:13
 * @Description: 收藏夹
 */
@RestController
public class CollectController {
    @Resource
    private CollectService collectService;

    /**
     * description: 获取收藏夹分类标签（带统计）
     *
     * @param userId
     * @return ResponseBean<ArticleVO>
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     */
    @GetMapping("/collect/tags/count")
    public ResponseBean<PublicArticleVo> getCollectTagCount(@RequestHeader("userId") Long userId) {
        List<CollectTagsDTO> data = collectService.getCollectTagCount(userId);
        return ResponseBean.ok(data);
    }

    /**
     * description: 获取收藏夹分类（纯展示）
     *
     * @param userId
     * @return ResponseBean<ArticleVO>
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     */
    @GetMapping("/collect/tags/list")
    public ResponseBean<PublicArticleVo> getCollectTagList(@RequestHeader("userId") Long userId) {
        List<CollectTagsDTO> data = collectService.getCollectTagList(userId);
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
    @PostMapping("/collect/tag/edit")
    public ResponseBean<PublicArticleVo> updateCollectTag(@RequestBody @Valid BaseRequest<CollectTagsDTO> baseRequest,
                                                           @RequestHeader("userId") Long userId) {
        Assert.hasLength(baseRequest.getData().getTitle(), "收藏分组名称不得为空");
        Long tagId = collectService.updateCollectTag(baseRequest.getData(), userId);
        return ResponseBean.ok(tagId);
    }

    /**
     * description: 删除 收藏夹分类标签
     *
     * @param userId
     * @return ResponseBean<ArticleVO>
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     */
    @PostMapping("/collect/tag/delete")
    public ResponseBean<PublicArticleVo> deleteCollectTag(@RequestBody @Valid BaseRequest<CollectTagsDTO> baseRequest,
                                                          @RequestHeader("userId") Long userId) {
        Assert.notNull(baseRequest.getData().getUid(), "收藏分组标签id不得为空");
        collectService.deleteCollectTag(baseRequest.getData(), userId);
        return ResponseBean.ok();
    }

    /**
     * description: 获取收藏夹内容
     *
     * @param userId
     * @return ResponseBean<ArticleVO>
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     */
    @PostMapping("/collect/list")
    public ResponseBean<PublicArticleVo> getCollectContentList(@RequestBody @Valid BaseRequest<CollectRequestDTO> baseRequest,
                                                               @RequestHeader("userId") Long userId) {
        List<CollectVo> collectList = collectService.getCollectList(baseRequest.getData(), userId);
        return ResponseBean.ok(collectList);
    }

    /**
     * description: 收藏标记
     *
     * @param userId
     * @return ResponseBean<ArticleVO>
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     */
    @PostMapping("/collect/mark")
    public ResponseBean<PublicArticleVo> markContent(@RequestBody @Valid BaseRequest<CollectTargetDTO> baseRequest,
                                                     @RequestHeader("userId") Long userId) {
        CollectTargetDTO data = baseRequest.getData();
        if (data.getTargetId() == null || data.getTargetType() == null) {
            throw new ServiceException(GlobalErrorCodes.BAD_REQUEST);
        }
        Long collectId = collectService.marksContent(data, userId);
        return ResponseBean.ok(collectId);
    }

    /**
     * description: 取消收藏
     *
     * @param userId
     * @return ResponseBean<ArticleVO>
     * @author: Lvhl
     * @date: 2024/9/16 11:56
     */
    @PostMapping("/collect/unmark")
    public ResponseBean<PublicArticleVo> cancelMarkContent(@RequestBody @Valid BaseRequest<CollectTargetDTO> baseRequest,
                                                           @RequestHeader("userId") Long userId) {
        CollectTargetDTO data = baseRequest.getData();
        if (data.getUid() == null) {
            throw new ServiceException(GlobalErrorCodes.BAD_REQUEST);
        }
        collectService.cancelMarkContent(data.getUid(), userId);
        return ResponseBean.ok();
    }
}
