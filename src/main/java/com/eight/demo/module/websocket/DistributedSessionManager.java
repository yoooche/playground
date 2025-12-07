package com.eight.demo.module.websocket;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.TextMessage;
import com.eight.demo.module.constant.RedisKey;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DistributedSessionManager {

    private final ConcurrentHashMap<Integer, WebSocketSession> localSessions = new ConcurrentHashMap<>();
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisPublisher publisher;
    private final String instanceId = generateInstanceId();

    public void addSession(Integer userId, WebSocketSession session) {
        localSessions.put(userId, session);
        var redisKey = RedisKey.WS_USER_SESSION.getValue().concat("::").concat(String.valueOf(userId));
        redisTemplate.opsForValue().set(redisKey, instanceId);
        log.info("User {} connected to instance {}", userId, instanceId);
    }

    public void removeSession(Integer userId) {
        localSessions.remove(userId);
        var redisKey = RedisKey.WS_USER_SESSION.getValue().concat("::").concat(String.valueOf(userId));
        redisTemplate.delete(redisKey);
        log.info("User {} disconnected from instance {}", userId, instanceId);
    }

    public boolean sendMessage(Integer userId, String message) {
        log.info("[{}] Trying to send message to user {}: {}", instanceId, userId, message);
        
        var session = localSessions.get(userId);
        if (session != null && session.isOpen()) {
            log.info("[{}] Found local session for user {}, sending directly", instanceId, userId);
            return sendLocalMessage(session, message);
        }
        
        log.info("[{}] No local session for user {}, checking Redis", instanceId, userId);
        var redisKey = RedisKey.WS_USER_SESSION.getValue().concat("::").concat(String.valueOf(userId));
        var targetInstanceId = redisTemplate.opsForValue().get(redisKey);
        
        log.info("[{}] Redis lookup for key '{}' returned: {}", instanceId, redisKey, targetInstanceId);
        
        if (targetInstanceId != null) {
            log.info("[{}] Publishing message to Redis for user {} on instance {}", instanceId, userId, targetInstanceId);
            publisher.pubMessage(userId, message);
            log.info("[{}] User {} message sent to instance {}", instanceId, userId, targetInstanceId);
            return true;
        }
        
        log.warn("[{}] No instance found for user {} in Redis", instanceId, userId);
        return false;
    }

    public void handleCrossInstancePush(Integer userId, String message) {
        var session = localSessions.get(userId);
        if (session != null && session.isOpen()) {
            sendLocalMessage(session, message);
            log.info("User {} message sent to cross instance {}", userId, instanceId);
        }
    }

    private boolean sendLocalMessage(WebSocketSession session, String message) {
        try {
            session.sendMessage(new TextMessage(message));
            return true;
        } catch (Exception e) {
            log.warn("Failed to send message", e);
            return false;
        }
    }

    private String generateInstanceId() {
        return "instance-" + UUID.randomUUID().toString();
    }
    
    public String getInstanceId() {
        return instanceId;
    }
}
