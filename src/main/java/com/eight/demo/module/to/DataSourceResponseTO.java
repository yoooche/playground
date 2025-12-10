package com.eight.demo.module.to;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DataSourceResponseTO {

    private String fileName;
    private String filePath;
    private List<DataSourceHeadersTO> headers;
}
