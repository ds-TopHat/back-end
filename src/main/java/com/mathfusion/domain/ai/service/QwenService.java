package com.mathfusion.domain.ai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mathfusion.domain.ai.dto.QwenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QwenService {

    @Value("${dashscope.api-key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper om = new ObjectMapper();

    public String callQwen25(List<String> imageUrls) {
        if (imageUrls == null || imageUrls.isEmpty()) {
            throw new IllegalArgumentException("imageUrls is empty");
        }
        if (imageUrls.size() == 1) {
            return callQwenProblemOnly(imageUrls.get(0));
        }
        if (imageUrls.size() == 2) {
            return callQwenProblemAndSolution(imageUrls.get(0), imageUrls.get(1));
        }
        throw new IllegalArgumentException("지원하지 않는 이미지 개수: " + imageUrls.size());
    }

    private String callQwenProblemOnly(String problemUrl) {
        String systemPrompt = """
            You are a math tutor. Look at the problem in the image and solve it step by step.
            Return JSON only: {"steps":[{"index":n,"expression":"...","reason":"..."}],"final_answer":"..."}
            """;
        return callQwenApi(problemUrl, systemPrompt, "Solve the problem and return JSON only.");
    }

    private String callQwenProblemAndSolution(String problemUrl, String solutionUrl) {
        String systemPrompt = """
            You are a math tutor. First image is the problem, second image is the student's solution.
            1) Diagnose issues in student's steps.
            2) Suggest fixes.
            3) Provide a correct solution.
            Return JSON only: {"diagnostics":[...],"fixes":[...],"corrected_solution":{"steps":[...],"final_answer":"..."}}
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