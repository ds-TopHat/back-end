package com.mathfusion.domain.explanation.controller;

import com.mathfusion.domain.explanation.service.ChatGPTService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/gpt")
public class ChatGPTController {
    private final ChatGPTService chatGPTService;


    @PostMapping("/explanation")
    public ResponseEntity<String> getExplanation(@RequestBody PromptRequest request) {
        String result = chatGPTService.prompt(request.getPrompt());
        return ResponseEntity.ok(result);
    }

    // DTO for request body
    public static class PromptRequest {
        private String prompt;

        public String getPrompt() {
            return prompt;
        }

        public void setPrompt(String prompt) {
            this.prompt = prompt;
        }
    }
}
