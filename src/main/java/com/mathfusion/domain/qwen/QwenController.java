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

//    @PostMapping("/analyze")
//    public ResponseEntity<?> analyzeImage(@RequestBody Map<String, String> request) {
//        String imageUrl = request.get("imageUrl");
//        try {
//            String json = qwenService.runMultiModalExample(imageUrl);
//            return ResponseEntity.ok().body(json);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
//        }
//    }
//@PostMapping("/analyze/from-s3")
//public ResponseEntity<?> analyzeFromS3() {
//    String uuid = UUID.randomUUID() + ".png";
//    Map<String, String> urls = s3Service.generateUploadAndDownloadUrls("uploads/" + uuid);
//    String downloadUrl = urls.get("downloadUrl");
//
//    try {
//        //중계 업로드 실행(QWEN이 받는 URL형태 : https://i.postimg.cc/tCx6CxZ2/image.jpg)
//        String publicUrl = uploadRelayService.uploadToPostimg(downloadUrl);
//        System.out.println("현재 Qwen에 전달되는 URL: " + publicUrl);
//        System.out.println("Presigned URL 확인: " + downloadUrl);
//        String result = qwenService.runMultiModalExample(publicUrl); // postimg URL을 Qwen에 전달
//
//        return ResponseEntity.ok(result);
//    } catch (Exception e) {
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .body("Qwen 처리 실패 : " + e.getMessage());
//    }

    @PostMapping("/analyze/from-s3")
    public ResponseEntity<?> analyzeFromS3(@RequestBody Map<String, String> body) {
        String downloadUrl = body.get("downloadUrl");

        try {
            String publicUrl = uploadRelayService.uploadToPostimg(downloadUrl);
            Thread.sleep(4000); // 4초 정도 지연해야할듯
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
