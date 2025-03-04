package com.lovbe.icharge.config;

import cn.hutool.core.codec.Base64;
import com.lovbe.icharge.common.exception.GlobalErrorCodes;
import com.lovbe.icharge.common.exception.ServiceErrorCodes;
import com.lovbe.icharge.common.exception.ServiceException;
import com.lovbe.icharge.common.util.CommonUtils;
import com.lovbe.icharge.common.util.JsonUtils;
import com.lovbe.icharge.common.util.SpringContextUtils;
import com.lovbe.icharge.entity.dto.WsMessageDTO;
import com.lovbe.icharge.service.ChatMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
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
     * @description: 关闭用户的所有会话
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


    /**
     * @description: 关闭单个用户的单个连接
     * @param: userId
     * @param: sessionId
     * @param: closeStatus
     * @author: lovbe0210
     * @date: 2025/3/2 18:47
     */
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

    /**
     * @description: 发送消息
     * @param: wsMessageDTO
     * @author: lovbe0210
     * @date: 2025/3/2 18:57
     */
    public static void sendMessage(WsMessageDTO wsMessageDTO) {
        if (wsMessageDTO == null || wsMessageDTO.getUserId() == null) {
            log.error("[发送ws消息] --- 消息发送失败，消息体为null|userId为null");
            return;
        }
        if (wsMessageDTO.getCallback() == null) {
            log.error("[发送ws消息] --- 消息发送失败，callback为null");
            return;
        }
        List<WebSocketSession> sessionList = sessions.get(String.valueOf(wsMessageDTO.getUserId()));
        if (CollectionUtils.isEmpty(sessionList)) {
            return;
        }
        sessionList.forEach(session -> {
            try {
                // 加密data部位
                String string = JsonUtils.toJsonString(wsMessageDTO.getData());
                String msgBody = CommonUtils.bitwiseInvert(Base64.encode(string));
                wsMessageDTO.setData(msgBody);
                session.sendMessage(new TextMessage(JsonUtils.toJsonString(wsMessageDTO)));
                if (log.isDebugEnabled()) {
                    log.debug("[发送ws消息] --- message: {}", string);
                }
            } catch (IOException e) {
                log.error("[发送ws消息] --- 消息发送失败，errorInfo: {}", e.toString());
            }
        });
    }
}
