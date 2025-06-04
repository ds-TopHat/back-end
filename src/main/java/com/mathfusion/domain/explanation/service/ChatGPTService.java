package com.mathfusion.domain.explanation.service;

import com.mathfusion.domain.explanation.config.ChatGPTConfig;
import com.mathfusion.domain.explanation.dto.ChatGPTRequest;
import com.mathfusion.domain.explanation.dto.ChatGPTResponse;
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

    public String prompt(String prompt){

        HttpHeaders headers = chatGPTConfig.httpHeaders();

        ChatGPTRequest chatGPTRequest = new ChatGPTRequest(model, prompt);

        HttpEntity<ChatGPTRequest> requestHttpEntity = new HttpEntity<>(chatGPTRequest, headers);

        ChatGPTResponse response = restTemplate.postForObject(url, requestHttpEntity, ChatGPTResponse.class);

        if(response == null || response.getChoices() == null || response.getChoices().isEmpty()){
            throw new RuntimeException();
        }
        return response.getChoices().get(0).getMessage().getContent();
    }


}

// 참고자료
// https://kylo8.tistory.com/entry/Chat-GPT-Spring-boot%EB%A5%BC-%ED%86%B5%ED%95%B4-GPT-API-Fine-tuning-prompt-%EC%88%98%ED%96%89%ED%95%98
// https://platform.openai.com/settings/organization/api-keys
// https://hyun-keepdeving.tistory.com/116
