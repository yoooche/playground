package com.eight.demo.module.controller;

import com.eight.demo.module.service.DataTransformService;
import com.eight.demo.module.to.ColumnMappingTO;
import com.eight.demo.module.to.DataSourceResponseTO;
import com.eight.demo.module.to.DataTransformResponseTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Data Transformer Functions")
@Validated
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
@RequestMapping(value = "/data-transform")
@RestController
public class DataTransformController {

    private final DataTransformService dataTransformService;

    @Operation(summary = "Upload CSV Files")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DataSourceResponseTO> uploadFile(@RequestPart("file") MultipartFile file) {
        var fileInfo = dataTransformService.uploadFile(file);
        return ResponseEntity.ok(dataTransformService.parseFileHeaders(fileInfo));
    }

    @Operation(summary = "Submit Mapping Rules")
    @PostMapping(value = "/mapping")
    public ResponseEntity<DataTransformResponseTO> submitDataMapping(@RequestBody ColumnMappingTO mappingTO) {
        return ResponseEntity.ok(dataTransformService.submitDataMapping(mappingTO));
    }
}
