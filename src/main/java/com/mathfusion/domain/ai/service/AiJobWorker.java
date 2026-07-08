package com.mathfusion.domain.ai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mathfusion.domain.ai.dto.ChatRequest;
import com.mathfusion.domain.ai.entity.AiJob;
import com.mathfusion.domain.ai.repository.AiJobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            AiJob job = markProcessing(jobId);

            ChatRequest request = objectMapper.readValue(job.getRequestJson(), ChatRequest.class);
            List<Map<String, String>> result = aiPipelineService.process(job.getUserEmail(), request);

            String resultJson = objectMapper.writeValueAsString(result);
            markDone(jobId, resultJson);

        } catch (Exception e) {
            log.error("AI Job 처리 실패. jobId={}", jobId, e);
            markFailed(jobId, e.getMessage());
        }
    }

    @Transactional
    public AiJob markProcessing(Long jobId) {
        AiJob job = aiJobRepository.findById(jobId).orElseThrow();
        job.start();
        return job;
    }

    @Transactional
    public void markDone(Long jobId, String resultJson) {
        AiJob job = aiJobRepository.findById(jobId).orElseThrow();
        job.complete(resultJson);
    }

    @Transactional
    public void markFailed(Long jobId, String errorMessage) {
        AiJob job = aiJobRepository.findById(jobId).orElseThrow();
        job.increaseRetryCount();
        job.fail(errorMessage);
    }
}