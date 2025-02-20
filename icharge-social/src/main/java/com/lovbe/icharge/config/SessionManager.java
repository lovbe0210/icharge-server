package com.lovbe.icharge.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: lovbe0210
 * @Date: 2025/2/16 15:46
 * @Description: 会话管理器
 */
@Slf4j
@Component
public class SessionManager {
    private static final Map<String, List<WebSocketSession>> sessions = new ConcurrentHashMap<>();

    /**
     * @description: 会话注册
     * @param: session
     * @author: lovbe0210
     * @date: 2025/2/20 0:06
     */
    public static void register(String userId, WebSocketSession session) {
        log.debug("[websocket新建连接] --- 连接信息->userId: {}, sessionId: {}", userId, session.getId());
        List<WebSocketSession> sessionList = sessions.get(userId);
        if (sessionList != null) {
            sessionList.add(session);
        } else {
            synchronized (sessions) {
                sessionList = sessions.get(userId);
                if (sessionList != null) {
                    sessionList.add(session);
                } else {
                    sessionList = new ArrayList<>();
                    sessionList.add(session);
                    sessions.put(userId, sessionList);
                }
            }
        }
    }

    /**
     * @description: 指定用户会话关闭
     * @param: userId
     * @param: closeStatus
     * @author: lovbe0210
     * @date: 2025/2/20 0:06
     */
    public static void closeExpiredSessions(String userId, CloseStatus closeStatus) {
        List<WebSocketSession> sessionList = sessions.get(userId);
        if (CollectionUtils.isEmpty(sessionList)) {
            return;
        }
        for (WebSocketSession session : sessionList) {
            if (session.isOpen()) {
                try {
                    session.close(closeStatus);
                } catch (IOException e) {
                    log.error("[websocket连接关闭] --- 关闭异常，errorInfo: {}", e.toString());
                }
            }
        }
        log.info("[websocket关闭连接] --- 连接关闭，userId: {}", userId);
        sessions.remove(userId);
    }


    public static void closeExpiredSession(String userId, String sessionId, CloseStatus closeStatus) {
        List<WebSocketSession> sessionList = sessions.get(userId);
        if (CollectionUtils.isEmpty(sessionList)) {
            return;
        }
        Iterator<WebSocketSession> iterator = sessionList.iterator();
        while (iterator.hasNext()) {
            WebSocketSession session = iterator.next();
            if (Objects.equals(session.getId(), sessionId)) {
                try {
                    if (session.isOpen()) {
                        session.close(closeStatus);
                    }
                } catch (IOException e) {
                    log.error("[websocket连接关闭] --- 关闭异常，errorInfo: {}", e.toString());
                }
                log.info("[websocket关闭连接] --- 连接关闭，userId: {}, sessionId: {}", userId, sessionId);
                iterator.remove();
                break;
            }
        }
        if (sessionList.size() == 0) {
            sessions.remove(userId);
        }
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
     * @return List<WebSocketSession>
     * @description: 获取当前登录用户的所有会话
     * @param: session
     * @author: lovbe0210
     * @date: 2025/2/20 0:07
     */
    public static List<WebSocketSession> getSessionList(String userId) {
        return sessions.get(userId);
    }
}
