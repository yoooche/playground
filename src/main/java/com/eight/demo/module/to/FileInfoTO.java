package com.eight.demo.module.to;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class FileInfoTO {

    private String fileName;
    private String filePath;
}
