package com.mathfusion.domain.ai.controller;

import com.mathfusion.domain.ai.dto.ChatRequest;
import com.mathfusion.domain.ai.rendering.SegmentSvgService;
import com.mathfusion.domain.ai.service.ChatGPTService;
import com.mathfusion.domain.ai.service.QwenService;
import com.mathfusion.domain.ai.service.UploadRelayService;
import com.mathfusion.domain.question.service.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
@Slf4j
@RestController
@RequestMapping("/api/v0/ai")
@RequiredArgsConstructor
public class AiController {

    private final QwenService qwenService;
    private final UploadRelayService uploadRelayService;
    private final ChatGPTService chatGPTService;
    private final QuestionService questionService;
    private final SegmentSvgService segmentSvgService;

    @Operation(
            summary = "AI에 수학문제 풀이 요청",
            description =
                    "1. 요청 시 반드시 `downloadUrls` 배열을 사용해야 합니다.\n\n" +
                            "문제 한 개만 넣을 경우:\n" +
                            "```json\n" +
                            "{\n" +
                            "  \"downloadUrls\": [\n" +
                            "    \"문제_이미지_URL\"\n" +
                            "  ]\n" +
                            "}\n" +
                            "```\n\n" +
                            "문제와 사용자 풀이 이미지를 함께 넣을 경우:\n" +
                            "```json\n" +
                            "{\n" +
                            "  \"downloadUrls\": [\n" +
                            "    \"문제_이미지_URL\",\n" +
                            "    \"사용자풀이_이미지_URL\"\n" +
                            "  ]\n" +
                            "}\n" +
                            "```\n\n" +
                            "2. 한 번 요청을 넣으면 네트워크 탭에 `200`과 `pending` 상태가 표시됩니다. " +
                            "AI 모델 응답이 오기까지 약 1~3분 정도 소요될 수 있으므로, 응답을 받기 전에 추가 요청을 보내면 처리 지연이 발생할 수 있습니다.\n\n" +
                            "3. s3Key도 넣어주세요."
    )
    @PostMapping("/chat")
    public ResponseEntity<?> qwenToDeepseekAndGpt(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ChatRequest req) {
        try {
            List<String> presignedUrls = req.normalized();
            if (presignedUrls.isEmpty()) {
                return ResponseEntity.badRequest().body("downloadUrl 또는 downloadUrls 중 하나는 반드시 필요합니다.");
            }

            List<String> publicUrls = new ArrayList<>();
            for (String url : presignedUrls) {
                String publicUrl = uploadRelayService.uploadToCloudinary(url);
                log.info("Presigned URL: {}", url);
                log.info("Qwen에 전달될 공개 URL: {}", publicUrl);
                publicUrls.add(publicUrl);
            }

            String qwenResult = qwenService.callQwen25(publicUrls);
//            log.info("Qwen Response:\n{}", qwenResult);

            List<Map<String, String>> parsed = chatGPTService.prompt(qwenResult);
//            log.info("GPT Parsed Result(size={}): {}", parsed.size(), parsed);

            // svg
            List<Map<String, String>> svgApplied = new ArrayList<>();
            for (Map<String, String> item : parsed) {
                Map<String, String> converted = new LinkedHashMap<>();
                for (Map.Entry<String, String> e : item.entrySet()) {
                    String k = e.getKey();
                    String v = e.getValue();
                    converted.put(k, v != null ? segmentSvgService.finalRender(v) : null);
                }
                svgApplied.add(converted);
            }
            // ------

            String s3Key = req.getS3Key();
            String email = (userDetails != null) ? userDetails.getUsername() : "anonymous";
            questionService.saveAiAnswer(email, svgApplied, s3Key);

            return ResponseEntity.ok(svgApplied);

        } catch (Exception e) {
            log.error("Ai 처리 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ai 처리 실패 : " + e.getMessage());
        }
    }
}