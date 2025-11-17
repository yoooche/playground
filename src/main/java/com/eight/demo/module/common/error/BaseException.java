package com.eight.demo.module.common.error;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseException extends RuntimeException {
    private String statusCode;

    public BaseException(String statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

}
