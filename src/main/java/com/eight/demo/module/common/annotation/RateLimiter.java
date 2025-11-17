package com.eight.demo.module.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.eight.demo.module.constant.Algorithm;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimiter {

    Algorithm algorithm() default Algorithm.FIXED_WINDOW;

    String key() default "rateLimiter";

    int limit() default 10;

    int window() default 60;

    boolean byClientIP() default false;

    String fallback() default "";

    Class<?> fallbackClass() default Void.class;

}
