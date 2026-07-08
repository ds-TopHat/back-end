package com.mathfusion.domain.ai.service;

import com.mathfusion.domain.ai.dto.ChatRequest;
import com.mathfusion.domain.ai.rendering.SegmentSvgService;
import com.mathfusion.domain.question.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AiPipelineService {

    private final QwenService qwenService;
    private final UploadRelayService uploadRelayService;
    private final ChatGPTService chatGPTService;
    private final QuestionService questionService;
    private final SegmentSvgService segmentSvgService;

    public List<Map<String, String>> process(String userEmail, ChatRequest req) throws Exception {
        List<String> presignedUrls = req.normalized();

        if (presignedUrls.isEmpty()) {
            throw new IllegalArgumentException("downloadUrl 또는 downloadUrls 중 하나는 반드시 필요합니다.");
        }

        List<String> publicUrls = new ArrayList<>();
        for (String url : presignedUrls) {
            publicUrls.add(uploadRelayService.uploadToCloudinary(url));
        }

        String qwenResult = qwenService.callQwen25(publicUrls);
        List<Map<String, String>> parsed = chatGPTService.prompt(qwenResult);

        List<Map<String, String>> svgApplied = new ArrayList<>();
        for (Map<String, String> item : parsed) {
            Map<String, String> converted = new LinkedHashMap<>();
            for (Map.Entry<String, String> entry : item.entrySet()) {
                String value = entry.getValue();
                converted.put(entry.getKey(), value != null ? segmentSvgService.finalRender(value) : null);
            }
            svgApplied.add(converted);
        }

        questionService.saveAiAnswer(userEmail, svgApplied, req.getS3Key());

        return svgApplied;
    }
}