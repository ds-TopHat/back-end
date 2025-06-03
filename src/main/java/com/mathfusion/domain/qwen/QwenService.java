package com.mathfusion.domain.qwen;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mathfusion.domain.qwen.dto.QwenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class QwenService {

    @Value("${dashscope.api-key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public String callQwen25(String imageUrl) {
        String endpoint = "https://dashscope-intl.aliyuncs.com/compatible-mode/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        // https://github.com/QwenLM/Qwen2.5-VL/issues/174
        String body = """
        {
        "model": "qwen2.5-vl-7b-instruct",
        "messages": [
            {
            "role": "system",
            "content": "You are an assistant that extracts and describes all math content from images. This includes equations, symbols, diagrams, graphs, and multiple choice options. Do not solve the problem. Describe everything in detail and use complete sentences."
            },
            {
            "role": "user",
            "content": [
                {
                "type": "image_url",
                "image_url": { "url": "%s" }
                },
                {
                "type": "text",
                "text": "Please convert the math problem in the image into clear and complete text, including all diagrams, graphs, and choices."
                }
            ]
            }
        ]
        }
        """.formatted(imageUrl);

        HttpEntity<String> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                endpoint,
                HttpMethod.POST,
                request,
                String.class
        );

        // JSON → DTO 파싱
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            QwenResponse qwenResponse = objectMapper.readValue(response.getBody(), QwenResponse.class);
            return qwenResponse.getChoices().get(0).getMessage().getContent();
        } catch (JsonProcessingException e) {
            // 예외 발생 시 로그 출력 또는 기본 응답 반환
            System.err.println("Qwen 응답 파싱 오류: " + e.getMessage());
            return "Qwen 응답을 이해하지 못했습니다.";
        }
    }
}