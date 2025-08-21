package com.mathfusion.domain.ai.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mathfusion.domain.ai.dto.ChatRequest;
import com.mathfusion.domain.ai.service.ChatGPTService;
import com.mathfusion.domain.ai.service.DeepSeekService;
import com.mathfusion.domain.ai.service.QwenService;
import com.mathfusion.domain.ai.service.UploadRelayService;
import com.mathfusion.domain.question.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final QwenService qwenService;
    private final UploadRelayService uploadRelayService;
    private final DeepSeekService deepSeekService;
    private final ChatGPTService chatGPTService;
    private final QuestionService questionService;

    @PostMapping("/chat")
    public ResponseEntity<?> qwenToDeepseekAndGpt(@RequestBody ChatRequest req) {
        try {

            List<String> presignedUrls = req.normalized();
            if (presignedUrls.isEmpty()) {
                return ResponseEntity.badRequest().body("downloadUrl 또는 downloadUrls 중 하나는 반드시 필요합니다.");
            }

            List<String> publicUrls = new ArrayList<>();
            for (String url : presignedUrls) {
                String publicUrl = uploadRelayService.uploadToCloudinary(url);
                System.out.println("Presigned URL: " + url);
                System.out.println("Qwen에 전달될 공개 URL: " + publicUrl);
                publicUrls.add(publicUrl);
            }

            String qwenResult;
            try {
                qwenResult = qwenService.callQwen25(publicUrls.toString());
            } catch (Throwable t) {
                qwenResult = qwenService.callQwen25(publicUrls.get(0));
            }

            String deepseekResult = deepSeekService.sendPrompt(qwenResult);
            String gptResult = chatGPTService.prompt(deepseekResult);

            String cleaned = gptResult;
            if (cleaned.contains("```")) {
                cleaned = cleaned.replaceAll("```json|```", "").trim();
            }

            ObjectMapper mapper = new ObjectMapper();
            List<Map<String, String>> result = mapper.readValue(cleaned, new TypeReference<>() {});

            questionService.saveAiAnswer(cleaned);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ai 처리 실패 : " + e.getMessage());
        }
    }


}