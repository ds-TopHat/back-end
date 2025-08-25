package com.mathfusion.domain.question.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mathfusion.domain.question.entity.Question;
import com.mathfusion.domain.question.repository.QuestionRepository;
import com.mathfusion.domain.unit.entity.Unit;
import com.mathfusion.domain.unit.repository.UnitRepository;
import com.mathfusion.domain.user.entity.User;
import com.mathfusion.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final UnitRepository unitRepository;
    private final UserRepository userRepository;
    private final ObjectMapper mapper;

    public Question saveAiAnswer(String email, String gptResult, String s3Key) throws Exception {
        // Markdown 제거
        String cleanedResponse = gptResult.replaceAll("```json|```", "").trim();

        // JSON 파싱
        List<Map<String, String>> parsed = mapper.readValue(cleanedResponse, new TypeReference<>() {});

        // 마지막 항목에서 type 추출
        Map<String, String> lastItem = parsed.get(parsed.size() - 1);
        String type = lastItem.get("type");
        if (type == null) {
            throw new IllegalArgumentException("AI JSON에서 type 정보를 찾을 수 없습니다.");
        }

        // Unit 조회
        Unit unit = unitRepository.findByType(type)
                .orElseThrow(() -> new IllegalArgumentException("Unit not found: " + type));

        // type 항목 제외하고 answer까지 포함
        List<Map<String, String>> filtered = parsed.stream()
                .filter(m -> !m.containsKey("type"))
                .toList();

        // User 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + email));

        // Builder로 Question 생성
        Question question = Question.builder()
                .user(user)
                .aiAnswer(mapper.writeValueAsString(filtered))
                .unit(unit)
                .problemImage(s3Key)
                .build();

        // DB 저장
        return questionRepository.save(question);
    }
}
