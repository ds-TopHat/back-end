package com.mathfusion.domain.ai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mathfusion.domain.ai.dto.ChatRequest;
import com.mathfusion.domain.ai.entity.AiJob;
import com.mathfusion.domain.ai.repository.AiJobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class AiJobService {

    private final AiJobRepository aiJobRepository;
    private final ObjectMapper objectMapper;
    private final AiJobWorker aiJobWorker;

    @Transactional
    public Long createJob(String userEmail, ChatRequest request) throws Exception {
        String requestJson = objectMapper.writeValueAsString(request);

        AiJob job = aiJobRepository.save(
                AiJob.builder()
                        .userEmail(userEmail)
                        .requestJson(requestJson)
                        .build()
        );

        aiJobWorker.process(job.getId());

        return job.getId();
    }

    @Transactional(readOnly = true)
    public AiJob getJob(Long jobId) {
        return aiJobRepository.findById(jobId)
                .orElseThrow(() -> new NoSuchElementException("AI Job을 찾을 수 없습니다."));
    }
}