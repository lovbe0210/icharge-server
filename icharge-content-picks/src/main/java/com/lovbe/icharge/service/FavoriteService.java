package com.lovbe.icharge.service;

import com.lovbe.icharge.entity.dto.FavoriteTagsDTO;
import com.lovbe.icharge.entity.dto.FavoriteTargetDTO;

import java.util.List;

/**
 * @Author: lovbe0210
 * @Date: 2024/11/25 23:19
 * @Description: 收藏夹
 */
public interface FavoriteService {
    /**
     * @description 收藏标记
     * @param[1] data
     * @param[2] userId
     * @author lovbe0210
     * @date 2024/11/25 23:46
     */
    void marksContent(FavoriteTargetDTO data, Long userId);

    /**
     * @description 取消收藏
     * @param[1] ftId
     * @param[2] userId
     * @author lovbe0210
     * @date 2024/11/26 0:19
     */
    void cancelMarkContent(Long ftId, Long userId);

    /**
     * @description 获取收藏夹分类标签
     * @param[1] userId
     * @return List<FavoriteTagsDTO>
     * @author lovbe0210
     * @date 2024/11/26 0:33
     */
    List<FavoriteTagsDTO> getFavoriteTagList(Long userId);

    /**
     * @description 分类标签编辑
     * @param[1] data
     * @param[2] userId
     * @author lovbe0210
     * @date 2024/11/26 0:44
     */
    void updateFavoriteTag(FavoriteTagsDTO data, Long userId);

    /**
     * @description 标签删除
     * @param[1] data
     * @param[2] userId
     * @author lovbe0210
     * @date 2024/11/26 0:54
     */
    void deleteFavoriteTag(FavoriteTagsDTO data, Long userId);
}
