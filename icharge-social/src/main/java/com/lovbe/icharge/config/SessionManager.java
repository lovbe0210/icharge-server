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

    public static void register(WebSocketSession session) {
        sessions.put(session.getId(), session);
    }

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
    }

    public static int getSessionCount() {
        return sessions.size();
    }
}
