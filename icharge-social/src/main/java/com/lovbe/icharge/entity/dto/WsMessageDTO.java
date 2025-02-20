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
     * 查询参数
     */
    @JsonIgnore
    private Map<String, Object> param;
    /**
     * 数据结果
     */
    private T data;

    public WsMessageDTO(int type, T t) {
        this.type = type;
        this.data = t;
    }
}
