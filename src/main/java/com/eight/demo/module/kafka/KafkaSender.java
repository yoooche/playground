package com.eight.demo.module.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.eight.demo.module.common.error.BaseException;
import com.eight.demo.module.constant.StatusCode;
import com.eight.demo.module.utils.JsonUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaSender {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void send(String topic, Object body) {
        try {
            var message = JsonUtils.toJson(body);
            kafkaTemplate.send(topic, message);
        } catch (Exception e) {
            throw new BaseException(StatusCode.UNKNOW_ERR, "Failed to send message: " + e.getMessage());
        }
    }
}
