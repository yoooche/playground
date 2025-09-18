package com.eight.demo.module.websocket;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.eight.demo.module.to.PushMessageData;
import com.eight.demo.module.utils.JsonUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisPublisher {

    private final RedisTemplate<String, Object> redisTemplate;

    public void pubMessage(Integer userId, String message) {
        long startTime = System.currentTimeMillis();
        try {
            var pushData = new PushMessageData();
            pushData.setUserId(userId);
            pushData.setMessage(message);
            
            var jsonMessage = JsonUtils.toJson(pushData);
            log.info("Publishing to Redis channel '{}': {}", WebSocketChannel.PUSH_MESSAGE, jsonMessage);
            
            redisTemplate.convertAndSend(WebSocketChannel.PUSH_MESSAGE, jsonMessage);
            
            long duration = System.currentTimeMillis() - startTime;
            log.info("Successfully published message to Redis in {}ms: userId={}, message={}", 
                    duration, userId, message);
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.warn("Failed to pub message after {}ms", duration, e);
        }
    }
}
