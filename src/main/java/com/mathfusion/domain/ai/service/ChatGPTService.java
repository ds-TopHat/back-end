package com.mathfusion.domain.ai.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mathfusion.domain.ai.config.ChatGPTConfig;
import com.mathfusion.domain.ai.dto.ChatGPTRequest;
import com.mathfusion.domain.ai.dto.ChatGPTResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatGPTService {

    private final ChatGPTConfig chatGPTConfig;
    private final RestTemplate restTemplate;
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${openai.model}")
    private String model;

    @Value("${openai.url}")
    private String url;

    public List<Map<String, String>> prompt(String englishMathContent) throws Exception {
        HttpHeaders headers = chatGPTConfig.httpHeaders();

        String systemPrompt = """
              너는 수학 문제 풀이 전문가이자 출력 포맷터다.
              \s
              출력 규칙:
              1) 무조건 JSON 배열만 출력한다. 코드펜스, 여분 텍스트, HTML, 설명문 금지.
              2) JSON 배열에는 반드시 다음 요소들이 포함되어야 한다:
                 - { "step n": "풀이 설명" } (n은 1부터 시작, 최소 3개 이상)
                 - { "answer": "최종 정답" }
                 - { "type": "<중학교 수학 단원 목록 중 하나>" }
                 - { "next_step": "step m" }  // 문자열 형식, 반드시 "step m"
              3) 모든 설명은 한국어로 자연스럽게 작성한다.
              4) { "type": ... } 값은 반드시 아래 목록 중 정확히 하나여야 한다.
                 [자연수의 성질, 정수와 유리수, 유리수와 소수, 문자와 식, 일차방정식, 부등식, 좌표평면과 그래프, 기본 도형, 도형의 이동, 자료의 수집과 정리, 평균과 중앙값, 가능성,
                 유리수와 순환소수, 식의 계산, 일차함수, 연립방정식, 도형의 성질, 도형의 작도와 합동, 삼각형과 사각형, 자료의 표현과 해석, 확률,
                 실수와 제곱근, 인수분해, 이차방정식, 이차함수, 피타고라스 정리, 원의 방정식, 통계의 이해와 활용]
              5) next_step 산정 규칙(아주 중요):
                 - RAW에 "Student Steps"가 있으면, 학생 풀이의 각 단계가 너의 "step k" 중 어느 것과 의미적으로 일치하는지 내부적으로 대조하라.
                 - 올바른 학생 단계의 마지막 인덱스를 c라 두면, next_step은 반드시 "step {c+1}" 이다.
                 - 일치하는 단계가 전혀 없으면 c=0으로 간주하고 next_step은 "step 1" 이다.
                 - 절대로 네가 방금 출력한 스텝 개수에 기반해 next_step을 증가시키지 마라.
               \s""";
        String userPrompt = String.format("""
                RAW 출력:
                <<<
                {%s}
                >>>
               \s
                TASKS:
               1) RAW 안에서 문제와 사용자의 풀이를 추출해라.
               2) 그 문제를 다시 풀어라 (DeepSeek 계산은 무시).
               3) 3~6 단계 풀이를 JSON 배열에 담아라.
               4) 마지막에 { "answer": "..." }, { "type": "..." }, { "next_step": "step n" } 을 반드시 붙여라.
       \s""", englishMathContent);

        ChatGPTRequest chatGPTRequest = new ChatGPTRequest(model, systemPrompt, userPrompt);
        HttpEntity<ChatGPTRequest> requestHttpEntity = new HttpEntity<>(chatGPTRequest, headers);

        ChatGPTResponse response = restTemplate.postForObject(url, requestHttpEntity, ChatGPTResponse.class);
        log.info("GPT response: {}", response);

        if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
            throw new RuntimeException("GPT 응답이 비어 있습니다.");
        }

        Object content = response.getChoices().get(0).getMessage().getContent();

        String s = (String) content;
        String str = s.trim();

        // ```json 제거
        str = str.replaceAll("(?s)^```json|```$", "").trim();

        // JSON 배열 추출
        int start = str.indexOf('[');
        int end = str.lastIndexOf(']');
        if (start >= 0 && end > start) {
            String jsonArray = str.substring(start, end + 1);
            return mapper.readValue(jsonArray, new TypeReference<List<Map<String, String>>>() {});
        } else {
            throw new IllegalArgumentException("GPT content에서 JSON 배열을 찾을 수 없습니다.");
        }
    }
}

// 참고자료
// https://kylo8.tistory.com/entry/Chat-GPT-Spring-boot%EB%A5%BC-%ED%86%B5%ED%95%B4-GPT-API-Fine-tuning-prompt-%EC%88%98%ED%96%89%ED%95%98
// https://platform.openai.com/settings/organization/api-keys
// https://hyun-keepdeving.tistory.com/116
