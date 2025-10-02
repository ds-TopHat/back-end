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

        String rulesEn = """
            Rules: Output ONLY a JSON array. No code fences, no extra text, no HTML, no tags.
            Allowed keys: "step n", "answer", "type", "next_step" only.
            At least 3 step objects. All text in English.
            The value for "type" must be EXACTLY ONE from this list:
            [Properties of Natural Numbers, Integers and Rational Numbers, Rational Numbers and Decimals, Letters and Expressions,
            Linear Equations, Inequalities, Coordinate Plane and Graphs, Basic Figures, Transformations of Figures, Data Collection and Organization,
            Mean and Median, Possibility, Rational Numbers and Repeating Decimals, Algebraic Operations, Linear Functions, Simultaneous Linear Equations,
            Properties of Figures, Constructions and Congruence, Triangles and Quadrilaterals, Data Representation and Interpretation, Probability,
            Real Numbers and Square Roots, Factorization, Quadratic Equations, Quadratic Functions, Pythagorean Theorem, Equation of a Circle,
            Understanding and Applications of Statistics]
            next_step policy: The user provides RAW student steps as an array of {index, expression}.
            Let c be the last index that is correct and meaningfully aligned with your generated steps; set next_step to "step {c+1}".
            If none align, use c=0 → next_step = "step 1". Do NOT base next_step on your step count.
        """;

        // ===== Few-shot 예시 =====
        String fewShot = """
            User: {"question": "Compute 2+3."}
            ---
            {"steps": [{"index":1,"expression":"Add 2 and 3"}]}
            Assistant: <think>(Simple arithmetic; produce at least 3 steps and the required fields)</think>
            <answer>
            [{"step 1": "Identify the addends: 2 and 3."}, {"step 2": "Compute 2 + 3 = 5."}, {"step 3": "Confirm the result."},
            {"answer": "5"}, {"type": "Integers and Rational Numbers"}, {"next_step": "step 2"}]
            </answer>
        """;

        String header = """
            A conversation between User and Assistant. The user asks a question, and the Assistant solves it.
            The Assistant must return ONLY the final JSON array inside <answer> </answer>.
            
        """;

        try {

            String combinedQuestion = question1;
            if (question2 != null) {
                combinedQuestion += "\n---\n" + question2;
            }

            //최종
            String systemPrompt = header
                    + fewShot + "\n"
                    + "User: " + combinedQuestion + "\n"
                    + "Assistant: <think>\n"
                    + rulesEn + "\n"
                    + "</think>\n"
                    + "<answer>\n"
                    + "You MUST begin with '[' and end with ']'.\n";


            log.info("Combined Question:\n{}", combinedQuestion);

            Map<String, String> request = new HashMap<>();
            request.put("question", combinedQuestion);
            request.put("systemPrompt", systemPrompt);

            log.info("DeepSeek Request Body:\n{}", request);

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