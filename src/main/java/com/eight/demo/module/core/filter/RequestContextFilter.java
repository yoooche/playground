package com.eight.demo.module.core.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.eight.demo.module.core.RequestContextHelper;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

@Component("customRequestContextFilter")
public class RequestContextFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        try {
            var requestInfo = new RequestInfo();
            requestInfo.setRequestId(UUID.randomUUID().toString().replace("-", ""));
            requestInfo.setIpAddress(getClientIp(httpRequest));
            
            var headers = new HashMap<String, String>();
            headers.put("X-Trace-ID", requestInfo.getRequestId());
            headers.put("X-Client-IP", requestInfo.getIpAddress());
            requestInfo.setHeaders(headers);

            RequestContextHelper.setRequestInfo(requestInfo);
            chain.doFilter(request, response);
        } finally {
            RequestContextHelper.clear();
        }
    }

    private String getClientIp(HttpServletRequest request) {
        var xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        var xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        return request.getRemoteAddr();
    }

}
