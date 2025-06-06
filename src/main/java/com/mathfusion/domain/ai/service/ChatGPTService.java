package com.mathfusion.domain.ai.service;

import com.mathfusion.domain.ai.config.ChatGPTConfig;
import com.mathfusion.domain.ai.dto.ChatGPTRequest;
import com.mathfusion.domain.ai.dto.ChatGPTResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class ChatGPTService {

    private final ChatGPTConfig chatGPTConfig;
    private final RestTemplate restTemplate;

    @Value("${openai.model}")
    private String model;

    @Value("${openai.url}")
    private String url;

    public String prompt(String englishMathContent) {
        HttpHeaders headers = chatGPTConfig.httpHeaders();

        String systemPrompt = "다음 영어 수학 풀이 내용을 보고, Step 1~N까지의 각 단계 설명을 추출해 주세요.\n" +
                "각 step을 자연스러운 한국어로 번역해 주세요.\n" +
                "HTML 해시태그는 제거해 주세요.\n" +
                "맨 위에 'json'이라는 단어도 빼주세요."+
                "출력 형식은 JSON 배열로 해 주세요. 각 단계는 다음과 같이 표현합니다: [ { \"step 1\": \"...\" }, { \"step 2\": \"...\" }, ... ]\n" +
                "그리고 마지막에는 반드시 다음 형식으로 정답을 배열 안에 포함시켜 주세요: { \"answer\": \"정답은 ab = 12입니다.\" }";
        String userPrompt = String.format("""
        \"\"\"%s\"\"\"
        """, englishMathContent);

        ChatGPTRequest chatGPTRequest = new ChatGPTRequest(model, systemPrompt, userPrompt);
        HttpEntity<ChatGPTRequest> requestHttpEntity = new HttpEntity<>(chatGPTRequest, headers);

        ChatGPTResponse response = restTemplate.postForObject(url, requestHttpEntity, ChatGPTResponse.class);

        if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
            throw new RuntimeException("GPT 응답이 비어 있습니다.");
        }

        return response.getChoices().get(0).getMessage().getContent();
    }
}

// 참고자료
// https://kylo8.tistory.com/entry/Chat-GPT-Spring-boot%EB%A5%BC-%ED%86%B5%ED%95%B4-GPT-API-Fine-tuning-prompt-%EC%88%98%ED%96%89%ED%95%98
// https://platform.openai.com/settings/organization/api-keys
// https://hyun-keepdeving.tistory.com/116
