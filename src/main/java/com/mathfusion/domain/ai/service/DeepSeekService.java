package com.mathfusion.domain.ai.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class DeepSeekService {

    private final WebClient webClient;

    public DeepSeekService(WebClient deepSeekWebClient) {
        this.webClient = deepSeekWebClient;
    }

    public String sendPrompt(String question) {

        String systemPrompt = """
                You are a math tutor.
                                
                Given a math problem and a student's solution in JSON format, do the following:
                1. Diagnose any incorrect or incomplete steps.
                2. Suggest how to fix them.
                3. Provide a full correct solution.
                                
                Return your answer in JSON. Do not explain.
                """;

        try {
            Map<String, String> request = Map.of("question", question);

            Mono<String> responseMono = webClient.post()
                    .uri("/solve")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(String.class);

            return responseMono.block();

        } catch (Exception e) {
            System.err.println("DeepSeek 호출 실패: " + e.toString());
            throw e;
        }
    }
}