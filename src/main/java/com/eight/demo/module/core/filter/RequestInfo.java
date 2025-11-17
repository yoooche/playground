package com.eight.demo.module.core.filter;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestInfo {

    private String requestId;

    private String ipAddress;

    private Map<String, String> headers;
}
