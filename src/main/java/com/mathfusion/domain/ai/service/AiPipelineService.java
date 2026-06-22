package com.mathfusion.domain.ai.service;

import com.mathfusion.domain.ai.dto.ChatRequest;
import com.mathfusion.domain.ai.rendering.SwitchSvgService;
import com.mathfusion.domain.question.service.QuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiPipelineService {

    private final QwenService qwenService;
    private final UploadRelayService uploadRelayService;
    private final ChatGPTService chatGPTService;
    private final QuestionService questionService;
    private final SwitchSvgService switchSvgService;

    public List<Map<String, String>> process(String email, ChatRequest req) throws Exception {
        List<String> presignedUrls = req.normalized();
        if (presignedUrls.isEmpty()) {
            throw new IllegalArgumentException("downloadUrl or downloadUrls is required");
        }

        List<String> publicUrls = new ArrayList<>();
        for (String url : presignedUrls) {
            String publicUrl = uploadRelayService.uploadToCloudinary(url);
            log.info("Presigned URL: {}", url);
            log.info("Public URL for Qwen: {}", publicUrl);
            publicUrls.add(publicUrl);
        }

        String qwenResult = qwenService.callQwen25(publicUrls);
        List<Map<String, String>> parsed = chatGPTService.prompt(qwenResult);

        List<Map<String, String>> svgApplied = new ArrayList<>();
        for (Map<String, String> item : parsed) {
            Map<String, String> converted = new LinkedHashMap<>();
            for (Map.Entry<String, String> e : item.entrySet()) {
                String k = e.getKey();
                String v = e.getValue();
                converted.put(k, v != null ? switchSvgService.replaceLatexWithSvg(v) : null);
            }
            svgApplied.add(converted);
        }

        questionService.saveAiAnswer(email, svgApplied, req.getS3Key());
        return svgApplied;
    }
}
