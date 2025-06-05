package com.mathfusion.domain.deepseek.service;

import com.mathfusion.domain.deepseek.dto.DeepSeekRequest;
import com.mathfusion.domain.deepseek.dto.DeepSeekResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
public class DeepSeekService {

    private final WebClient webClient;

    public DeepSeekService(WebClient deepSeekWebClient) {
        this.webClient = deepSeekWebClient;
    }

    public Map<String, String> sendPrompt(String question) {
        DeepSeekRequest request = new DeepSeekRequest(question);

        try {
            Mono<DeepSeekResponse> responseMono = webClient.post()
                    .uri("/solve")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(DeepSeekResponse.class);

            DeepSeekResponse response = responseMono.block();

            Map<String, String> resultMap = new HashMap<>();
            resultMap.put("response", response != null ? response.getResponse() : "응답 없음");
            return resultMap;

        } catch (Exception e) {
            System.err.println("DeepSeek 호출 실패: " + e.toString());
            throw e;
        }
    }


    private String extractBetweenTags(String text, String startTag, String endTag) {
        int start = text.indexOf(startTag);
        int end = text.indexOf(endTag);
        if (start != -1 && end != -1 && start < end) {
            return text.substring(start + startTag.length(), end).trim();
        }
        return null;
    }


}
