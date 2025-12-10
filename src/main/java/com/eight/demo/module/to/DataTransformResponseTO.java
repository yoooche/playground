package com.eight.demo.module.to;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DataTransformResponseTO {

    private String jobId;
    private Integer status;
    private String message;
}
