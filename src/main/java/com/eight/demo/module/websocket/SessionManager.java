package com.eight.demo.module.websocket;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SessionManager {

    private final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    public void addSession(String sessionId, WebSocketSession session) {
        sessions.put(sessionId, session);
        log.info("User {} connected", sessionId);
    }

    public void removeSession(Integer userId) {
        sessions.remove(userId);
        log.info("User {} disconnected", userId);
    }

    public void sendMessage(String sessionId, String message) {
        var session = sessions.get(sessionId);
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(message));
            } catch (Exception e) {
                log.warn("Failed to send message to user {}", sessionId, e);
                sessions.remove(sessionId);
            }
        }
    }

    public boolean isOnline(Integer userId) {
        var session = sessions.get(userId);
        return session != null && session.isOpen();
    }
}
