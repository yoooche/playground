package com.eight.demo.module.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.eight.demo.module.service.NotificationService;
import com.eight.demo.module.to.NotificationTO;
import com.eight.demo.module.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaConsumer {

    private final NotificationService notificationService;

    @KafkaListener(topics = Topic.NOTIFICATION)
    public void processMailNotification(String content) {
        log.info("Receive topic [{}] and message=[{}]", Topic.NOTIFICATION, content);
        try {
            var notification = JsonUtils.fromJson(content, NotificationTO.class);
            notificationService.pushToUsers(notification);
        } catch (Exception e) {
            log.warn("Failed to process mail task", e);
        }
    }
}
