package com.eight.demo.module.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * WebSocket 處理器
 * 負責處理 WebSocket 的連接、消息、斷開
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationHandler extends TextWebSocketHandler {

    private final DistributedSessionManager sessionManager;

    /**
     * 建立連接：提取 userId 並註冊到分布式 session 管理器
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        var userId = getUserId(session);
        if (userId != null) {
            sessionManager.addSession(userId, session);
        } else {
            log.warn("UserId doesn't exist, closing connection");
            session.close();
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        var userId = getUserId(session);
        if (userId != null) {
            sessionManager.removeSession(userId);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        var userId = getUserId(session);
        log.debug("Received from user {}: {}", userId, message.getPayload());
        session.sendMessage(new TextMessage("Echo: " + message.getPayload()));
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        var userId = getUserId(session);
        log.error("Transport error for user {}", userId, exception);
        if (userId != null) {
            sessionManager.removeSession(userId);
        }
    }

    private Integer getUserId(WebSocketSession session) {
        try {
            var uri = session.getUri();
            if (uri != null && uri.getQuery() != null) {
                var params = uri.getQuery().split("&");
                for (var param : params) {
                    var kv = param.split("=");
                    if (kv.length == 2 && "userId".equals(kv[0])) {
                        return Integer.parseInt(kv[1]);
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Error extracting userId", e);
        }
        return null;
    }
}
