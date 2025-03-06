package com.lovbe.icharge.entity.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @Author: lovbe0210
 * @Date: 2025/2/21 0:04
 * @Description: websocket消息类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WsMessageDTO<T> {
    /**
     * 消息类型 0心跳 1会话相关 2消息相关
     */
    private int type;
    /**
     * 前端回调方法
     */
    private String callback;
    /**
     * 查询参数
     */
//    @JsonIgnore
    private Map<String, Object> param;
    /**
     * 当前用户id
     */
    @JsonIgnore
    private Long userId;
    /**
     * 数据结果
     */
    private T data;

    public WsMessageDTO(int type, T t) {
        this.type = type;
        this.data = t;
    }

    public WsMessageDTO(Long userId, int type, String callback) {
        this.userId = userId;
        this.type = type;
        this.callback = callback;
    }

    public WsMessageDTO(int type, String callback, Long userId, T data) {
        this.type = type;
        this.callback = callback;
        this.userId = userId;
        this.data = data;
    }
}
