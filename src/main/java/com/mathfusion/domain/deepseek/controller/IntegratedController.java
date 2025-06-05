package com.mathfusion.domain.deepseek.controller;


import com.mathfusion.domain.deepseek.service.DeepSeekService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/integrated")
@RequiredArgsConstructor
public class IntegratedController {

    private final DeepSeekService deepSeekService;

    // 문제 텍스트 받아서 deepseek에 넣고 답 받는 API
    @PostMapping("/solve-from-text")
    public ResponseEntity<?> solveFromText(@RequestBody Map<String, String> body) {
        try {
            String question = body.get("question");
            if (question == null || question.isEmpty()) {
                return ResponseEntity.badRequest().body("question 필드가 필요합니다.");
            }

            Map<String, String> responseMap = deepSeekService.sendPrompt(question);
            String answer = responseMap.get("response");

            return ResponseEntity.ok(Map.of(
                    "response", answer
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("처리 실패 : " + (e.getMessage() == null ? e.toString() : e.getMessage()));
        }
    }

}
