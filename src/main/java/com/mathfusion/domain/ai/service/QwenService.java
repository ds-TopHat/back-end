package com.mathfusion.domain.ai.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mathfusion.domain.ai.dto.QwenResponse;
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
        // https://www.alibabacloud.com/help/en/model-studio/compatibility-of-openai-with-dashscope
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
              "content": "You are a helpful assistant that looks at solution steps to math problems in images and rewrites them in clear, complete, and grammatically correct English. Do not solve anything new. Only describe the solution steps exactly as shown in the image."

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
                  "text": "Extract math steps in JSON. Do not solve. Just structure the student's solution."
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