package com.mathfusion.domain.qwen;

import com.mathfusion.service.S3Service;
import com.mathfusion.service.UploadRelayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/dashscope")
@RequiredArgsConstructor
public class QwenController {

    private final QwenService qwenService;
    private final S3Service s3Service;
    private final UploadRelayService uploadRelayService;

    @PostMapping("/analyze/from-s3")
    public ResponseEntity<?> analyzeFromS3(@RequestBody Map<String, String> body) {
        String downloadUrl = body.get("downloadUrl");

        try {
            String publicUrl = uploadRelayService.uploadToCloudinary(downloadUrl);
            System.out.println("현재 Qwen에 전달되는 URL: " + publicUrl);
            System.out.println("Presigned URL 확인: " + downloadUrl);

            String result = qwenService.runMultiModalExample(publicUrl);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Qwen 처리 실패 : " + e.getMessage());
        }
    }

}
