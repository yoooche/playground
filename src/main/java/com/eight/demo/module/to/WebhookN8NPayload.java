package com.eight.demo.module.to;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class WebhookN8NPayload extends ColumnMappingTO {

    private String jobId;
}
