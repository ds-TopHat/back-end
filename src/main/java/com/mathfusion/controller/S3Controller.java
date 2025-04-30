package com.mathfusion.controller;

import com.mathfusion.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class S3Controller {

    private final S3Service s3Service;

    @GetMapping("/s3/presigned")
    public Map<String, String> getPresignedUrl() {
        String uuidFileName = UUID.randomUUID() + ".png";
        String presignedUrl = s3Service.generatePresignedUrl("uploads/" + uuidFileName);
        return Map.of("url", presignedUrl);
    }
}
