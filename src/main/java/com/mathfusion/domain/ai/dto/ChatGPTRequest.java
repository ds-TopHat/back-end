package com.mathfusion.domain.ai.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class ChatGPTRequest {

    private String model;
    private double temperature;
    private List<ChatMessage> messages;
    private int n;

    public ChatGPTRequest(String model, String systemPrompt, String userPrompt) {
        this.model = model;
        this.temperature = 0.0;
        this.messages = new ArrayList<>();
        this.messages.add(new ChatMessage("system", systemPrompt)); // 시스템이 받는 프롬프트
        this.messages.add(new ChatMessage("user", userPrompt)); // 사용자가 받는 프롬프트
        this.n = 1;
    }
}
