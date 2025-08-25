package com.mathfusion.domain.ai.service;

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

    public String sendPrompt(String question) {

        String systemPrompt = """
            You are a math tutor.
            Input is a JSON with a problem and student's steps.
            Return a single minified JSON:
            {"diagnosis":[{"step_index":n,"issue":str}], "fixes":[{"step_index":n,"suggestion":str}], "solution":{"steps":[{"index":n,"expression":str,"reason":str}], "final_answer":str}}.
            Do not add text outside JSON.
        """;

        try {
            Map<String, String> request = new HashMap<>();
            request.put("systemPrompt", systemPrompt);
            request.put("question", question);

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