package com.eight.demo.module.websocket;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SessionManager {

    private final ConcurrentHashMap<Integer, WebSocketSession> sessions = new ConcurrentHashMap<>();

    public void addSession(Integer userId, WebSocketSession session) {
        sessions.put(userId, session);
        log.info("User {} connected", userId);
    }

    public void removeSession(Integer userId) {
        sessions.remove(userId);
        log.info("User {} disconnected", userId);
    }

    public boolean sendMessage(Integer userId, String message) {
        var session = sessions.get(userId);
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(message));
                return true;
            } catch (Exception e) {
                log.warn("Failed to send message to user {}", userId, e);
                sessions.remove(userId);
                return false;
            }
        }
        return false;
    }

    public boolean isOnline(Integer userId) {
        var session = sessions.get(userId);
        return session != null && session.isOpen();
    }
}
