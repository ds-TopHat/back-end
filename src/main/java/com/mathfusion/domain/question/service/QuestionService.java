package com.mathfusion.domain.question.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mathfusion.domain.question.entity.Question;
import com.mathfusion.domain.question.repository.QuestionRepository;
import com.mathfusion.domain.unit.entity.Unit;
import com.mathfusion.domain.unit.repository.UnitRepository;
import com.mathfusion.domain.user.entity.User;
import com.mathfusion.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final UnitRepository unitRepository;
    private final UserRepository userRepository;
    private final ObjectMapper mapper;

    public Question saveAiAnswer(String email, List<Map<String, String>> gptResult, String s3Key) throws Exception {
        if (gptResult == null || gptResult.isEmpty()) {
            throw new IllegalArgumentException("AI JSON에서 content가 비어 있습니다.");
        }

        // 마지막 항목에서 type 추출
        Map<String, String> lastItem = gptResult.get(gptResult.size() - 2);
        String type = gptResult.stream()
                .filter(m -> m.containsKey("type"))
                .map(m -> m.get("type"))
                .findFirst()
                .orElse(null);

        if (type == null) {
            throw new IllegalArgumentException("AI JSON에서 type 정보를 찾을 수 없습니다.");
        }

        Unit unit = unitRepository.findByType(type)
                .orElseThrow(() -> new IllegalArgumentException("Unit not found: " + type));

        List<Map<String, String>> filtered = gptResult.stream()
                .filter(m -> !m.containsKey("type") && !m.containsKey("next_step"))
                .toList();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + email));

        Question question = Question.builder()
                .user(user)
                .aiAnswer(mapper.writeValueAsString(filtered))
                .unit(unit)
                .problemImage(s3Key)
                .build();

        return questionRepository.save(question);
    }
}
