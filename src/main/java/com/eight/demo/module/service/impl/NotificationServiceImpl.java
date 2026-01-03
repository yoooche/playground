package com.eight.demo.module.service.impl;

import com.eight.demo.module.cache.RedisHelper;
import com.eight.demo.module.constant.RedisKey;
import com.eight.demo.module.websocket.SessionManager;
import org.springframework.stereotype.Service;

import com.eight.demo.module.service.NotificationService;
import com.eight.demo.module.to.NotificationTO;
import com.eight.demo.module.websocket.DistributedSessionManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final DistributedSessionManager sessionManager;
    private final RedisHelper redisHelper;
    private final SessionManager sessionLocalManager;

    @Override
    public void pushToUsers(NotificationTO notification) {
        var userIds = notification.getUserIds();
        var success = 0;
        var total = userIds.size();
        var msg = notification.getTitle() + " " + notification.getContent();

        for (var userId : userIds) {
            var pushSuccess = sessionManager.sendMessage(userId, msg);
            if (pushSuccess) {
                success++;
            } else {
                saveOfflineMessage(userId, msg);
            }
        }

        log.info("Push result: {}/{} users received notification", success, total);
    }

    @Override
    public void pushToSession(String sessionId) {
        sessionLocalManager.sendMessage(sessionId, "Test");
        log.info("TEST PUSH");
    }

    private void saveOfflineMessage(Integer userId, String msg) {
        try {
            redisHelper.rightPush(RedisKey.PUSH_OFFLINE.getValue(), "offline: " + msg, userId);
        } catch (Exception e) {
            log.warn("Failed to save offline message for user {}", userId, e);
        }
    }

}
