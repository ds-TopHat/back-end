package com.mathfusion.domain.ai.controller;

import com.mathfusion.domain.ai.dto.AiJobCreateResponse;
import com.mathfusion.domain.ai.dto.AiJobResponse;
import com.mathfusion.domain.ai.dto.ChatRequest;
import com.mathfusion.domain.ai.service.AiJobService;
import com.mathfusion.domain.ai.service.AiPipelineService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v0/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiPipelineService aiPipelineService;
    private final AiJobService aiJobService;

    @Operation(
            summary = "AI math solving request",
            description = "Synchronous API. It waits until Qwen, DeepSeek, GPT, SVG conversion, and DB save are complete."
    )
    @PostMapping("/chat")
    public ResponseEntity<?> qwenToDeepseekAndGpt(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ChatRequest req) {
        try {
            String email = (userDetails != null) ? userDetails.getUsername() : "anonymous";
            var response = aiPipelineService.process(email, req);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("AI processing failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("AI processing failed : " + e.getMessage());
        }
    }

    @Operation(
            summary = "Create async AI math solving job",
            description = "Creates a job and immediately returns jobId. Poll /api/v0/ai/jobs/{jobId} for status and result."
    )
    @PostMapping("/chat/jobs")
    public ResponseEntity<?> createAiJob(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ChatRequest req) {
        try {
            String email = (userDetails != null) ? userDetails.getUsername() : "anonymous";
            AiJobCreateResponse response = aiJobService.createJob(email, req);
            return ResponseEntity.accepted().body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("AI job creation failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("AI job creation failed : " + e.getMessage());
        }
    }

    @Operation(
            summary = "Get async AI job status",
            description = "Returns PENDING, PROCESSING, DONE, or FAILED. DONE responses include the final AI result."
    )
    @GetMapping("/jobs/{jobId}")
    public ResponseEntity<?> getAiJob(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long jobId) {
        try {
            String email = (userDetails != null) ? userDetails.getUsername() : "anonymous";
            AiJobResponse response = aiJobService.getJob(email, jobId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            log.error("AI job lookup failed. jobId={}", jobId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("AI job lookup failed : " + e.getMessage());
        }
    }
}
