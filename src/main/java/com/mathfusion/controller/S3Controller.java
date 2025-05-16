package com.mathfusion.controller;

import com.mathfusion.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor // final이 붙거나 @NotNull 이 붙은 필드의 생성자를 자동 생성해주는 롬복 어노테이션
public class S3Controller {

    private final S3Service s3Service;

    @GetMapping("/s3/presigned")
    public Map<String, String> getBothPresignedUrls() {
        String uuidFileName = UUID.randomUUID() + ".png";
        return s3Service.generateUploadAndDownloadUrls("uploads/" + uuidFileName);
    }

}
