package com.lovbe.icharge.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: lovbe0210
 * @Date: 2025/2/16 15:46
 * @Description: 会话管理器
 */
@Slf4j
@Component
public class SessionManager {
    private static final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    /**
     * @description: 会话注册
     * @param: session
     * @author: lovbe0210
     * @date: 2025/2/20 0:06
     */
    public static void register(WebSocketSession session) {
        log.debug("[websocket新建连接] --- 连接注册，userId: {}", session.getId());
        sessions.put(session.getId(), session);
    }

    /**
     * @description: 会话关闭
     * @param: userId
     * @param: closeStatus
     * @author: lovbe0210
     * @date: 2025/2/20 0:06
     */
    public static void closeExpiredSession(String userId, CloseStatus closeStatus) {
        WebSocketSession session = sessions.get(userId);
        if (session != null && session.isOpen()) {
            try {
                session.close(closeStatus);
                sessions.remove(userId);
            } catch (IOException e) {
                // 日志处理
                log.error("[websocket连接关闭] --- 关闭异常，errorInfo: {}", e.toString());
            }
        } else if (session != null) {
            sessions.remove(userId);
        }
        log.info("[websocket关闭连接] --- 连接关闭，userId: {}", userId);
    }

    /**
     * @description: 获取会话总数
     * @return: int
     * @author: lovbe0210
     * @date: 2025/2/20 0:06
     */
    public static int getSessionCount() {
        return sessions.size();
    }

    /**
     * @return
     * @description: 获取所有会话
     * @param: session
     * @author: lovbe0210
     * @date: 2025/2/20 0:07
     */
    public static Map<String, WebSocketSession> getSessionMap() {
        return sessions;
    }

    /**
     * @return WebSocketSession
     * @description: 获取指定用户的会话
     * @param: userId
     * @author: lovbe0210
     * @date: 2025/2/20 0:07
     */
    public static WebSocketSession getSessionMap(Long userId) {
        return sessions.get(userId);
    }
}
