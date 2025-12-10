package com.eight.demo.module.to;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MappingRuleTO {

    private String source;
    private String target;
    private String transformType;
    private String rule;
    private String outputType;
}
