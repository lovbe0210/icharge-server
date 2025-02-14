package com.lovbe.icharge.service;

import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.entity.MusicInfoVo;
import com.lovbe.icharge.entity.MusicPlayDTO;

import java.util.List;

/**
 * @Author: lovbe0210
 * @Date: 2025/2/13 23:57
 * @Description: 音乐播放相关
 */
public interface MusicPlayService {
    /**
     * @description: 获取播放列表
     * @param: userId
     * @return: java.util.List<com.lovbe.icharge.entity.MusicInfoVo>
     * @author: lovbe0210
     * @date: 2025/2/13 23:50
     */
    List<MusicInfoVo> getMusicPlayList(Long userId);

    /**
     * @description: 修改音乐播放列表
     * @param: baseRequest
     * @param: userId
     * @author: lovbe0210
     * @date: 2025/2/14 0:00
     */
    void updateMusicPlayList(BaseRequest<List<MusicPlayDTO>> baseRequest, Long userId);

    /**
     * @description: 添加音乐
     * @param: baseRequest
     * @param: userId
     * @author: lovbe0210
     * @date: 2025/2/15 1:27
     */
    void addMusic2PlayList(BaseRequest<MusicPlayDTO> baseRequest, Long userId);

    /**
     * @description: 播放列表删除
     * @param: baseRequest
     * @param: userId
     * @author: lovbe0210
     * @date: 2025/2/15 1:33
     */
    void deleteMusicPlayList(BaseRequest<MusicPlayDTO> baseRequest, Long userId);
}
