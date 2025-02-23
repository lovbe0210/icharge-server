package com.lovbe.icharge.config;

import com.lovbe.icharge.common.enums.SysConstant;
import com.lovbe.icharge.common.util.JsonUtils;
import com.lovbe.icharge.common.util.redis.RedisKeyConstant;
import com.lovbe.icharge.common.util.redis.RedisUtil;
import com.lovbe.icharge.entity.dto.WsMessageDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @Author: lovbe0210
 * @Date: 2025/2/16 13:25
 * @Description: webSocket相关配置
 */
@Slf4j
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(messageHandler(), "/socket")
                .addInterceptors(handshakeInterceptor())
                .setAllowedOrigins("http://localhost", "https://www.10020210.xyz");
    }

    @Bean
    public HandshakeInterceptor handshakeInterceptor() {
        return new HandshakeInterceptor() {
            @Override
            public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
                HttpHeaders headers = request.getHeaders();
                if (headers.isEmpty() || CollectionUtils.isEmpty(headers.get("cookie"))) {
                    log.error("[ws连接握手] --- cookie获取失败，握手失败");
                    response.setStatusCode(HttpStatusCode.valueOf(403));
                    return false;
                }
                String[] acToken = new String[1];
                List<String> cookie = headers.get("cookie");
                cookie.stream()
                        .filter(ck -> ck.indexOf(SysConstant.ACCESS_TOKEN) != -1)
                        .findFirst()
                        .ifPresent(ck -> {
                            if (ck.indexOf(";") != -1) {
                                String[] split = ck.split("; ");
                                for (String s : split) {
                                    if (s.indexOf(SysConstant.ACCESS_TOKEN) != -1) {
                                        acToken[0] = s.split("=")[1];
                                        break;
                                    }
                                }
                            } else {
                                String[] split = ck.split("=");
                                if (SysConstant.ACCESS_TOKEN.equals(split[0])) {
                                    acToken[0] = split[1];
                                }
                            }
                        });
                if (acToken[0] == null) {
                    log.error("[ws连接握手] --- cookie获取失败，握手失败");
                    response.setStatusCode(HttpStatusCode.valueOf(403));
                    return false;
                }
                Object userId = RedisUtil.get(RedisKeyConstant.getAccessTokenKey(acToken[0]));
                if (userId != null && !userId.equals("")) {
                    headers.set(SysConstant.USERID, String.valueOf(userId));
                    return true;
                }
                log.error("[ws连接握手] --- 用户登录信息已过期，握手失败");
                response.setStatusCode(HttpStatusCode.valueOf(403));
                return false;
            }

            @Override
            public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

            }
        };
    }

    @Bean
    public WebSocketHandler messageHandler() {
        return new TextWebSocketHandler() {
            @Override
            protected void handleTextMessage(WebSocketSession session, TextMessage message) {
                String payload = message.getPayload();
                try {
                    WsMessageDTO wsMessageDTO = JsonUtils.parseObject(payload, WsMessageDTO.class);
                    if (wsMessageDTO == null) {
                        return;
                    }
                    switch (wsMessageDTO.getType()) {
                        case 0 -> {
                            WsMessageDTO<Object> messageDTO = new WsMessageDTO<>(0, "pong");
                            String string = JsonUtils.toJsonString(messageDTO);
                            session.sendMessage(new TextMessage(string));
                        }
                    }
                } catch (Exception e) {
                    log.error("[ws消息处理] --- 消息解析失败，errorInfo: {}", e.toString());
                }
            }

            @Override
            public void afterConnectionEstablished(WebSocketSession session) {
                HttpHeaders headers = session.getHandshakeHeaders();
                String userId = headers.getFirst(SysConstant.USERID);
                if (!StringUtils.hasLength(userId)) {
                    // 认证阶段设置的userId丢失，断开连接
                    try {
                        session.close(CloseStatus.SERVER_ERROR);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    // 注册连接
                    SessionManager.register(userId, session);
                }
            }

            @Override
            public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
                HttpHeaders headers = session.getHandshakeHeaders();
                String userId = headers.getFirst(SysConstant.USERID);
                SessionManager.closeExpiredSession(userId, session.getId(), status);
            }
        };
    }
}
