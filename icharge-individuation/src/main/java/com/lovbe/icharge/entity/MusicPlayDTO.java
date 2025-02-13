package com.lovbe.icharge.entity;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @Author: lovbe0210
 * @Date: 2025/2/14 0:01
 * @Description: MS
 */
@Data
public class MusicPlayDTO {
    /**
     * 外部平台音乐id
     */
    @NotBlank(message = "音乐id不得为空")
    private String musicId;
    /**
     * 音乐名称
     */
    @NotBlank(message = "音乐名称不得为空")
    private String musicName;
    /**
     * 音乐作者
     */
    private String author;
    /**
     * 平台code 1网易云 2QQ音乐 3酷狗
     */
    @NotBlank(message = "平台id不得为空")
    private String platformCode;
}
