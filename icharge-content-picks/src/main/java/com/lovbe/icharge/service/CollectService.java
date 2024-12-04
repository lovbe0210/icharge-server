package com.lovbe.icharge.service;

import com.lovbe.icharge.entity.dto.CollectRequestDTO;
import com.lovbe.icharge.entity.dto.CollectTagsDTO;
import com.lovbe.icharge.entity.dto.CollectTargetDTO;
import com.lovbe.icharge.entity.vo.CollectVo;

import java.util.List;

/**
 * @Author: lovbe0210
 * @Date: 2024/11/25 23:19
 * @Description: 收藏夹
 */
public interface CollectService {
    /**
     * @return
     * @description 收藏标记
     * @param[1] data
     * @param[2] userId
     * @author lovbe0210
     * @date 2024/11/25 23:46
     */
    Long marksContent(CollectTargetDTO data, Long userId);

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
    List<CollectTagsDTO> getCollectTagCount(Long userId);
    
    /** 
     * @description: 获取标签列表
     * @param: Long
     * @return: List<CollectTagsDTO>
     * @author: lovbe0210
     * @date: 2024/12/3 16:45
     */
    List<CollectTagsDTO> getCollectTagList(Long userId);

    /**
     * @return
     * @description 分类标签编辑
     * @param[1] data
     * @param[2] userId
     * @author lovbe0210
     * @date 2024/11/26 0:44
     */
    Long updateCollectTag(CollectTagsDTO data, Long userId);

    /**
     * @description 标签删除
     * @param[1] data
     * @param[2] userId
     * @author lovbe0210
     * @date 2024/11/26 0:54
     */
    void deleteCollectTag(CollectTagsDTO data, Long userId);

    /**
     * @return List<CollectTagsVo>
     * @description 获取收藏内容
     * @param[1] data
     * @param[2] userId
     * @author lovbe0210
     * @date 2024/11/28 23:20
     */
    List<CollectVo> getCollectList(CollectRequestDTO data, Long userId);

    /**
     * @description: 获取收藏详情
     * @param: Long
     * @return: CollectVo
     * @author: lovbe0210
     * @date: 2024/12/4 15:34
     */
    CollectVo getCollectInfo(Long collectId, Long userId);
}
