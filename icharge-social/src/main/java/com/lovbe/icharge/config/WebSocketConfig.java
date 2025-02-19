package com.lovbe.icharge.config;

import com.lovbe.icharge.common.exception.ServiceException;
import com.lovbe.icharge.common.util.SpringContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;

/**
 * @Author: lovbe0210
 * @Date: 2025/2/16 13:25
 * @Description: MS
 */
@Slf4j
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(messageHandler(), "/ws")
                .setAllowedOrigins("*");
    }

    @Bean
    public WebSocketHandler messageHandler() {
        return new TextWebSocketHandler() {
            @Override
            protected void handleTextMessage(WebSocketSession session, TextMessage message) {
                log.error(message.toString());
                // 业务处理逻辑
                try {
                    String messageBody = """
                            {
                                "message": "ping -> pong",
                                "connects": %s
                            }
                            """.formatted(SessionManager.getSessionCount());
                    session.sendMessage(new TextMessage(messageBody));
                    Map<String, WebSocketSession> sessionMap = SessionManager.getSessionMap();
                    for (Map.Entry<String, WebSocketSession> entry : sessionMap.entrySet()) {
                        String userId = entry.getKey();
                        WebSocketSession wsSession = entry.getValue();
                        String replyBody = """
                                {
                                    "userId": %s
                                }
                                """.formatted(userId);
                        wsSession.sendMessage(new TextMessage(replyBody));
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void afterConnectionEstablished(WebSocketSession session) {
                SessionManager.register(session);
            }

            @Override
            public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
                Map<String, Object> attributes = session.getAttributes();
                SessionManager.closeExpiredSession(session.getId(), status);
            }
        };
    }
}
