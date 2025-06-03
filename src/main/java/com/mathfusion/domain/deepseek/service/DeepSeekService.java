package com.mathfusion.domain.deepseek.service;

import com.mathfusion.domain.deepseek.dto.DeepSeekRequest;
import com.mathfusion.domain.deepseek.dto.DeepSeekResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class DeepSeekService {

    private final WebClient webClient;

    @Value("${deepseek.api.url}")
    private String deepSeekUrl;

    public DeepSeekService(WebClient deepSeekWebClient) {
        this.webClient = deepSeekWebClient;
    }

    public String sendPrompt(String prompt) {
        DeepSeekRequest request = new DeepSeekRequest(prompt);

        Mono<DeepSeekResponse> responseMono = webClient.post()
                .uri(deepSeekUrl)  // 예: "/generate"
                .bodyValue(request)
                .retrieve()
                .bodyToMono(DeepSeekResponse.class);

        // 블로킹 방식 (결과를 기다림, 비동기 원하면 .subscribe() 사용)
        DeepSeekResponse response = responseMono.block();

        return response != null ? response.getResult() : "응답 없음";
    }
}
