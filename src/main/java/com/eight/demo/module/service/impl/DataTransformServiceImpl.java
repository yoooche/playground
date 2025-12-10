package com.eight.demo.module.service.impl;

import com.eight.demo.module.common.error.BaseException;
import com.eight.demo.module.constant.StatusCode;
import com.eight.demo.module.service.DataTransformService;
import com.eight.demo.module.to.ColumnMappingTO;
import com.eight.demo.module.to.DataSourceHeadersTO;
import com.eight.demo.module.to.DataSourceResponseTO;
import com.eight.demo.module.to.DataTransformResponseTO;
import com.eight.demo.module.to.FileInfoTO;
import com.eight.demo.module.to.WebhookN8NPayload;
import com.opencsv.CSVReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class DataTransformServiceImpl implements DataTransformService {

    private final RestTemplate restTemplate;
    @Value("${spring.file.upload-dir}")
    private String uploadDir;

    @Override
    public FileInfoTO uploadFile(MultipartFile file) {
        try {
            var uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            var originalFileName = file.getOriginalFilename();
            if (originalFileName == null) {
                throw new BaseException(StatusCode.UNKNOW_ERR, "Failed to extract original file name");
            }
            var targetPath = uploadPath.resolve(originalFileName);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            return FileInfoTO.builder()
                    .fileName(originalFileName)
                    .filePath(targetPath.toString())
                    .build();
        } catch (IOException ie) {
            throw new BaseException(StatusCode.UNKNOW_ERR, ie.getMessage());
        }
    }

    @Override
    public DataSourceResponseTO parseFileHeaders(FileInfoTO fileInfo) {
        try (CSVReader reader = new CSVReader(new FileReader(fileInfo.getFilePath()))) {
            List<String[]> allRows = reader.readAll();
            String[] headers = allRows.getFirst();
            var headerTO = new ArrayList<DataSourceHeadersTO>();
            Arrays.stream(headers).sequential()
                    .forEach(header -> {
                        headerTO.add(DataSourceHeadersTO.builder()
                                .headerName(header)
                                .build());
                    });

            return DataSourceResponseTO.builder()
                    .fileName(fileInfo.getFileName())
                    .filePath(fileInfo.getFilePath())
                    .headers(headerTO)
                    .build();
        } catch (Exception e) {
            throw new BaseException(StatusCode.UNKNOW_ERR, "解析 CSV 失敗: " + e.getMessage());
        }
    }

    @Override
    public DataTransformResponseTO submitDataMapping(ColumnMappingTO to) {
        var jobId = UUID.randomUUID().toString();
        var payload = WebhookN8NPayload.builder()
                .jobId(jobId)
                .brandId(to.getBrandId())
                .filePath(to.getFilePath())
                .mappings(to.getMappings())
                .build();

        var resp = restTemplate.postForEntity("http://35.212.144.79:5678/webhook/data-transfer", payload, String.class);
        return DataTransformResponseTO.builder()
                .jobId(jobId)
                .status(1)
                .message(resp.getBody())
                .build();
    }
}
