package com.lovbe.icharge.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lovbe.icharge.common.model.base.BaseEntity;
import lombok.Data;

/**
 * @Author: lovbe0210
 * @Date: 2025/2/13 23:42
 * @Description: MS
 */
@Data
@TableName("i_music_playlist")
public class MusicInfoVo extends BaseEntity {
    /**
     * 外部平台音乐id
     */
    private String musicId;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 音乐名称
     */
    private String musicName;
    /**
     * 音乐作者
     */
    private String author;
    /**
     * 平台code 1网易云 2QQ音乐 3酷狗
     */
    private String platformCode;
    /**
     * 是否收藏 0否1是
     */
    private int isLike;
}
