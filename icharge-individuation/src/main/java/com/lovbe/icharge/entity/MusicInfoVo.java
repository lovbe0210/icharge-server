package com.lovbe.icharge.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lovbe.icharge.common.model.base.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: lovbe0210
 * @Date: 2025/2/13 23:42
 * @Description: MS
 */
@Data
@TableName("i_music_playlist")
@Accessors(chain = true)
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
     * 歌曲时长
     */
    private int duration;
    /**
     * 音乐封面
     */
    private String musicCover;
    /**
     * 平台code 1网易云 2QQ音乐 3酷狗
     */
    private String platformCode;
    /**
     * 是否收藏 0否1是
     */
    private int isLike;
}
