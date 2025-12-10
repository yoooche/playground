package com.eight.demo.module.controller;

import com.eight.demo.module.service.DataTransformService;
import com.eight.demo.module.to.ColumnMappingTO;
import com.eight.demo.module.to.DataSourceResponseTO;
import com.eight.demo.module.to.DataTransformResponseTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Validated
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
@RequestMapping(value = "/data-transform")
@RestController
public class DataTransformController {

    private final DataTransformService dataTransformService;

    @PostMapping(value = "/upload")
    public ResponseEntity<DataSourceResponseTO> uploadFile(@RequestParam("file") MultipartFile file) {
        var fileInfo = dataTransformService.uploadFile(file);
        return ResponseEntity.ok(dataTransformService.parseFileHeaders(fileInfo));
    }

    @PostMapping(value = "/mapping")
    public ResponseEntity<DataTransformResponseTO> submitDataMapping(@RequestBody ColumnMappingTO mappingTO) {
        return ResponseEntity.ok(dataTransformService.submitDataMapping(mappingTO));
    }
}
