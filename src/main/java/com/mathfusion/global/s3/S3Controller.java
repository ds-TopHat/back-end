package com.mathfusion.global.s3;

import com.mathfusion.global.s3.dto.PresignedBatchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor // final이 붙거나 @NotNull 이 붙은 필드의 생성자를 자동 생성해주는 롬복 어노테이션
public class S3Controller {

    private final S3Service s3Service;

    // 문제 1장 반환 : /s3/presigned
    // 문제 1장 + 사용자 문제풀이 1장 : /s3/presigned?count=2
    @GetMapping("/s3/presigned")
    public PresignedBatchResponse getPresignedUrls(
            @RequestParam(name = "count", defaultValue = "1") int count
    ) {
        count = Math.max(1, Math.min(count, 2));

        List<String> ups = new ArrayList<>();
        List<String> downs = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            var pair = s3Service.generateUploadAndDownloadUrls("uploads/" + UUID.randomUUID() + ".png");
            ups.add(pair.getUploadUrl());
            downs.add(pair.getDownloadUrl());
        }

        return new PresignedBatchResponse(ups, downs);
    }
}
