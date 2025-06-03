package com.mathfusion.domain.deepseek.controller;

import com.mathfusion.domain.deepseek.service.DeepSeekService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/solve")
@RequiredArgsConstructor
public class DeepSeekController {

    private final DeepSeekService deepSeekService;

    @PostMapping
    @Operation(summary = "수학 문제 풀이 요청", description = "질문을 딥시크에 전달해 수학 문제를 풉니다.")
    public ResponseEntity<String> solve(@RequestBody Map<String, String> payload) {
        String question = payload.get("question");
        System.out.println("DeepSeekController solve() 진입");
        String response = deepSeekService.sendPrompt(question);
        return ResponseEntity.ok(response);
    }
}
