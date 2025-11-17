package com.eight.demo.module.core;

import org.springframework.stereotype.Component;
import com.eight.demo.module.core.filter.RequestInfo;

@Component
public class RequestContextHelper {

    private static final ThreadLocal<RequestInfo> REQ_CONTEXT = new ThreadLocal<>();

    public static void setRequestInfo(RequestInfo requestInfo) {
        REQ_CONTEXT.set(requestInfo);
    }

    public static String getClientIp() {
        var info = REQ_CONTEXT.get();
        return info != null ? info.getIpAddress() : null;
    }

    public static void clear() {
        REQ_CONTEXT.remove();
    }
}
