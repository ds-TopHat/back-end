package com.mathfusion.domain.ai.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mathfusion.domain.ai.dto.ChatRequest;
import com.mathfusion.domain.ai.entity.AiJob;
import com.mathfusion.domain.ai.repository.AiJobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiJobWorker {

    private final AiJobRepository aiJobRepository;
    private final AiPipelineService aiPipelineService;
    private final ObjectMapper objectMapper;

    @Async("aiTaskExecutor")
    public void process(Long jobId) {
        try {
            JobPayload payload = markProcessing(jobId);
            List<Map<String, String>> result = aiPipelineService.process(payload.email(), payload.request());
            markDone(jobId, objectMapper.writeValueAsString(result));
        } catch (Exception e) {
            log.error("AI job failed. jobId={}", jobId, e);
            markFailed(jobId, e.getMessage());
        }
    }

    protected JobPayload markProcessing(Long jobId) throws Exception {
        AiJob job = aiJobRepository.findWithUserById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("AI job not found: " + jobId));
        job.markProcessing();
        ChatRequest req = toChatRequest(job);
        aiJobRepository.save(job);
        return new JobPayload(job.getUser().getEmail(), req);
    }

    protected void markDone(Long jobId, String resultJson) {
        AiJob job = aiJobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("AI job not found: " + jobId));
        job.markDone(resultJson);
        aiJobRepository.save(job);
    }

    protected void markFailed(Long jobId, String message) {
        aiJobRepository.findById(jobId).ifPresent(job -> {
            job.markFailed(message);
            aiJobRepository.save(job);
        });
    }

    private ChatRequest toChatRequest(AiJob job) throws Exception {
        List<String> downloadUrls = objectMapper.readValue(
                job.getDownloadUrlsJson(),
                new TypeReference<List<String>>() {}
        );

        ChatRequest req = new ChatRequest();
        req.setDownloadUrls(downloadUrls);
        req.setS3Key(job.getS3Key());
        return req;
    }

    private record JobPayload(String email, ChatRequest request) {
    }
}
