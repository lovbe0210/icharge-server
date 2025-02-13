package com.lovbe.icharge.controller;

import com.lovbe.icharge.common.enums.SysConstant;
import com.lovbe.icharge.common.model.base.BaseRequest;
import com.lovbe.icharge.common.model.base.ResponseBean;
import com.lovbe.icharge.entity.MusicInfoVo;
import com.lovbe.icharge.entity.MusicPlayDTO;
import com.lovbe.icharge.service.MusicPlayService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: lovbe0210
 * @Date: 2025/2/13 23:56
 * @Description: 音乐播放相关
 */
@RestController
public class MusicPlayController {
    @Resource
    private MusicPlayService musicPlayService;

    /**
     * @description: 获取音乐播放列表
     * @param: userId
     * @return: com.lovbe.icharge.common.model.base.ResponseBean
     * @author: lovbe0210
     * @date: 2025/2/9 17:28
     */
    @GetMapping("/music/list/get")
    public ResponseBean getMusicPlayList(@RequestHeader(SysConstant.USERID) Long userId) {
        List<MusicInfoVo> musicList =  musicPlayService.getMusicPlayList(userId);
        return ResponseBean.ok(musicList);
    }

    /**
     * @description: 获取音乐播放列表
     * @param: userId
     * @return: com.lovbe.icharge.common.model.base.ResponseBean
     * @author: lovbe0210
     * @date: 2025/2/9 17:28
     */
    @PostMapping("/music/list/update")
    public ResponseBean updateMusicPlayList(@RequestBody @Valid BaseRequest<List<MusicPlayDTO>> baseRequest,
                                            @RequestHeader(SysConstant.USERID) Long userId) {
        musicPlayService.updateMusicPlayList(baseRequest, userId);
        return ResponseBean.ok();
    }
}
