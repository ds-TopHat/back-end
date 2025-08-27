package com.mathfusion.domain.ai.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.lang.Nullable;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class DeepSeekService {

    private final WebClient webClient;

    public DeepSeekService(WebClient deepSeekWebClient) {
        this.webClient = deepSeekWebClient;
    }

    public String sendText(String question1, @Nullable String question2) {

        // 프롬프트 분기
        String systemPrompt;
        if (question2 == null) {
            systemPrompt = """
                You are a math tutor.
                Input is a JSON with a problem and student's steps.
                Return a single minified JSON:
                {"diagnosis":[{"step_index":n,"issue":str}], "fixes":[{"step_index":n,"suggestion":str}], "solution":{"steps":[{"index":n,"expression":str,"reason":str}], "final_answer":str}}.
                Do not add text outside JSON.
            """;
        } else {
            systemPrompt = """
                You are a math tutor.
             Input: JSON with a problem and student's steps.\s
             Note: The input text uses the delimiter "\\n---\\n". The part before "\\n---\\n" is the problem, and the part after "\\n---\\n" is the student's steps.
             Tasks:\s
               (1) diagnose incorrect steps,
               (2) suggest fixes,
               (3) provide full correct solution,
               (4) mark the next step after the student's last correct step.
             Return ONE minified JSON EXACTLY in this shape:
             {"diagnosis":[{"step_index":n,"issue":str}],
              "fixes":[{"step_index":n,"suggestion":str}],
              "solution":{"steps":[{"index":n,"expression":str,"reason":str}],"final_answer":str},
              "next_step_index":n}
             No text outside JSON.
            """;
        }

        try {
            // question1과 question2를 string으로 합치기
            String combinedQuestion = question1;
            if (question2 != null) {
                combinedQuestion += "\n---\n" + question2; // 구분자 넣기
            }

            // combinedQuestion 로그
            log.info("Combined Question:\n{}", combinedQuestion);

            Map<String, String> request = new HashMap<>();
            request.put("question", combinedQuestion);
            request.put("systemPrompt", systemPrompt);

            // 요청 직전 전체 request 로그
            log.info("DeepSeek Request Body:\n{}", request);

            Mono<String> responseMono = webClient.post()
                    .uri("/solve")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(String.class);

            String finalResult = responseMono.block();

            return finalResult;

        } catch (Exception e) {
            System.err.println("DeepSeek 호출 실패: " + e.toString());
            throw e;
        }
    }
}