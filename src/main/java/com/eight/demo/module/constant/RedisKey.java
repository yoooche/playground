package com.eight.demo.module.constant;

import lombok.Getter;

@Getter
public enum RedisKey {

    RATE_LIMITER("rate-limiter"),
    WS_USER_SESSION("ws-user-session");

    private String value;

    RedisKey(String value) {
        this.value = value;
    }
}
