package com.eight.demo.module.websocket;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import com.eight.demo.module.to.PushMessageData;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketMessageListner implements MessageListener {

    private final DistributedSessionManager sessionManager;
    private final StringRedisSerializer serializer = new StringRedisSerializer();

    @Override
    public void onMessage(Message message, byte[] pattern) {
        long startTime = System.currentTimeMillis();
        try {
            String messageBody = serializer.deserialize(message.getBody());
            long deserializeTime = System.currentTimeMillis() - startTime;
            
            log.info("[{}] Received Redis message in {}ms: {}", 
                    sessionManager.getInstanceId(), deserializeTime, messageBody);
            
            handlePushMessage(messageBody);
            
            long totalTime = System.currentTimeMillis() - startTime;
            log.info("[{}] Total Redis message processing time: {}ms", 
                    sessionManager.getInstanceId(), totalTime);
        } catch (Exception e) {
            long totalTime = System.currentTimeMillis() - startTime;
            log.error("[{}] Error processing Redis message after {}ms", 
                    sessionManager.getInstanceId(), totalTime, e);
        }
    }

    public void handlePushMessage(String message) {
        log.info("[{}] Processing message in WebSocketMessageListner: {}", sessionManager.getInstanceId(), message);
        
        try {
            var om = new ObjectMapper();
            var pushData = om.readValue(message, PushMessageData.class);
            log.info("Successfully parsed JSON: userId={}, message={}", pushData.getUserId(), pushData.getMessage());
            sessionManager.handleCrossInstancePush(pushData.getUserId(), pushData.getMessage());
        } catch (Exception e) {
            log.warn("[{}] Failed to handle push message: {}", sessionManager.getInstanceId(), message, e);
        }
    }
}
