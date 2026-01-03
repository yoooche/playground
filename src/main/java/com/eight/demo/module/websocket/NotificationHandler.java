package com.eight.demo.module.websocket;

import com.eight.demo.module.utils.JsonUtils;
import java.util.Map;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationHandler extends TextWebSocketHandler {

    private final SessionManager sessionManager;

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
        var sessionId = session.getId();
        sessionManager.addSession(sessionId, session);
        var payload = Map.of("sessionId", sessionId);
        session.sendMessage(new TextMessage(JsonUtils.toJson(payload)));
        log.info("New connection established, sessionId: {}", sessionId);
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {
        var userId = getUserId(session);
        if (userId != null) {
            sessionManager.removeSession(userId);
        }
    }

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, TextMessage message) throws Exception {
        var userId = getUserId(session);
        log.debug("Received from user {}: {}", userId, message.getPayload());
        session.sendMessage(new TextMessage("Echo: " + message.getPayload()));
    }

    @Override
    public void handleTransportError(@NonNull WebSocketSession session, @NonNull Throwable exception) {
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
