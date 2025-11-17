package com.eight.demo.module.common.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.eight.demo.module.constant.StatusCode;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseExceptionResponse> handleException(Exception e) {
        var baseExceptionResponse = BaseExceptionResponse.of(e);
        return new ResponseEntity<>(baseExceptionResponse, HttpStatus.OK);
    }

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<BaseExceptionResponse> handleBaseException(BaseException e) {
        var baseExceptionResponse = BaseExceptionResponse.of(e);
        return new ResponseEntity<>(baseExceptionResponse, getHttpStatus(e.getStatusCode()));
    }

    private HttpStatus getHttpStatus(String statusCode) {
        return switch (statusCode) {
            case StatusCode.TOO_MANY_REQUEST -> HttpStatus.TOO_MANY_REQUESTS;
            case StatusCode.REQ_PARAM_ERR, StatusCode.USER_NOT_FOUND -> HttpStatus.BAD_REQUEST;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}
