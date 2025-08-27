package com.mathfusion.domain.ai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mathfusion.domain.ai.dto.QwenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class QwenService {

    @Value("${dashscope.api-key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper om = new ObjectMapper();

    private final DeepSeekService deepSeekService;

    public String callQwen25(List<String> imageUrls) {
        if (imageUrls == null || imageUrls.isEmpty()) {
            throw new IllegalArgumentException("imageUrls is empty");
        }
        if (imageUrls.size() == 1) {
            String qwenResult = callQwenProblemOnly(imageUrls.get(0));
            return deepSeekService.sendText(qwenResult, null);
        }

        if (imageUrls.size() == 2) {
            String qwenResult1 = callQwenProblemOnly(imageUrls.get(0));
            String qwenResult2 = callQwenProblemAndSolution(imageUrls.get(0), imageUrls.get(1));
            // String finalResult = deepSeekService.sendText(qwenResult1, qwenResult2);

            try {
                // question1과 question2를 string으로 합치기
                String combinedQuestion = qwenResult1;
                if (qwenResult2 != null) {
                    combinedQuestion += "\n---\n" + qwenResult2; // 구분자 넣기
                }

                // combinedQuestion 로그
                log.info("Qwen Combined Question:\n{}", combinedQuestion);

                return combinedQuestion;
            } catch (Exception e) {
                System.err.println("이미지url 2개일 때 Qwen 호출 실패: " + e.toString());
                throw e;
            }
        }

        throw new IllegalArgumentException("지원하지 않는 이미지 개수: " + imageUrls.size());
    }

    private String callQwenProblemOnly(String problemUrl) {
        String systemPrompt = """
            You are a helpful assistant that extracts math problems from images and rewrites them in clear, complete, and grammatically correct English. Do not solve the problem. Describe the entire problem exactly as shown in the image, including any answer choices, diagrams, graphs, tables, or geometric figures. All output must be a single line of plain English text, with no bullet points, no Markdown, no LaTeX, and no line breaks. The text should be formatted to fit inside a JSON string as a value for the 'question' key.
            """;
        return callQwenApi(problemUrl, systemPrompt, "Solve the problem and return JSON only.");
    }

    private String callQwenProblemAndSolution(String problemUrl, String solutionUrl) {
        String systemPrompt = """
                First image = problem (context only, ignore completely in output).
                Second image = student's solution (only this should be structured).
                Return ONE minified JSON that starts with { and ends with }: {"steps":[{"index":n,"expression":str}]}.
                Use exactly the keys 'steps', 'index', and 'expression'.
                Do not solve or add new steps. Transcribe exactly what is visible in the student's solution.
                No explanations, no natural language, only JSON.
            """;
        return callQwenApi(List.of(problemUrl, solutionUrl), systemPrompt,
                "Compare the student's solution with the problem and return JSON only.");
    }

    private String callQwenApi(String imageUrl, String systemPrompt, String userText) {
        return callQwenApi(List.of(imageUrl), systemPrompt, userText);
    }

    private String callQwenApi(List<String> imageUrls, String systemPrompt, String userText) {
        String endpoint = "https://dashscope-intl.aliyuncs.com/compatible-mode/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        try {
            // message content 구성
            ArrayNode userContent = om.createArrayNode();
            for (String url : imageUrls) {
                ObjectNode img = om.createObjectNode();
                img.put("type", "image_url");
                ObjectNode u = om.createObjectNode();
                u.put("url", url);
                img.set("image_url", u);
                userContent.add(img);
            }
            ObjectNode text = om.createObjectNode();
            text.put("type", "text");
            text.put("text", userText);
            userContent.add(text);

            ObjectNode sysMsg = om.createObjectNode();
            sysMsg.put("role", "system");
            sysMsg.put("content", systemPrompt);

            ObjectNode userMsg = om.createObjectNode();
            userMsg.put("role", "user");
            userMsg.set("content", userContent);

            ArrayNode messages = om.createArrayNode();
            messages.add(sysMsg);
            messages.add(userMsg);

            ObjectNode root = om.createObjectNode();
            root.put("model", "qwen2.5-vl-7b-instruct");
            root.set("messages", messages);

            HttpEntity<String> request = new HttpEntity<>(om.writeValueAsString(root), headers);
            ResponseEntity<String> response = restTemplate.exchange(endpoint, HttpMethod.POST, request, String.class);

            QwenResponse q = om.readValue(response.getBody(), QwenResponse.class);
            return q.getChoices().get(0).getMessage().getContent();

        } catch (Exception e) {
            System.err.println("Qwen 호출 실패: " + e.getMessage());
            return "{\"error\":\"Qwen failed\",\"message\":\"" + e.getMessage() + "\"}";
        }
    }
}