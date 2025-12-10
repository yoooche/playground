package com.eight.demo.module.service;

import com.eight.demo.module.to.ColumnMappingTO;
import com.eight.demo.module.to.DataSourceResponseTO;
import com.eight.demo.module.to.DataTransformResponseTO;
import com.eight.demo.module.to.FileInfoTO;
import org.springframework.web.multipart.MultipartFile;

public interface DataTransformService {

    FileInfoTO uploadFile(MultipartFile file);
    DataSourceResponseTO parseFileHeaders(FileInfoTO filePath);
    DataTransformResponseTO submitDataMapping(ColumnMappingTO mappingTO);
}
