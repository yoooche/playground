package com.eight.demo.module.to;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DataSourceHeadersTO {

    private String fileSource;
    private String headerName;
    private String type;
    private List<Object> example;
}
