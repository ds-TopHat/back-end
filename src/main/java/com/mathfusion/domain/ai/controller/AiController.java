package com.mathfusion.domain.ai.controller;

import com.mathfusion.domain.ai.service.DeepSeekService;
import com.mathfusion.domain.ai.service.ChatGPTService;
import com.mathfusion.domain.ai.service.QwenService;
import com.mathfusion.domain.ai.service.UploadRelayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final QwenService qwenService;
    private final UploadRelayService uploadRelayService;
    private final DeepSeekService deepSeekService;
    private final ChatGPTService chatGPTService;

    @PostMapping("/chat")
    public ResponseEntity<?> qwenToDeepseekAndGpt(@RequestBody Map<String, String> body) {
        String downloadUrl = body.get("downloadUrl");

        try {
            String publicUrl = uploadRelayService.uploadToCloudinary(downloadUrl);
            System.out.println("현재 Qwen에 전달되는 URL: " + publicUrl);
            System.out.println("Presigned URL 확인: " + downloadUrl);

            String qwenResult = qwenService.callQwen25(publicUrl);
            String deepseekResult = deepSeekService.sendPrompt(qwenResult);
            String gptResult = chatGPTService.prompt(deepseekResult);


            return ResponseEntity.ok(gptResult);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Qwen 처리 실패 : " + e.getMessage());
        }
    }

}
