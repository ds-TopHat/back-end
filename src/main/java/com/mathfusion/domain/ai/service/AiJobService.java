package com.mathfusion.domain.ai.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mathfusion.domain.ai.dto.AiJobCreateResponse;
import com.mathfusion.domain.ai.dto.AiJobResponse;
import com.mathfusion.domain.ai.dto.ChatRequest;
import com.mathfusion.domain.ai.entity.AiJob;
import com.mathfusion.domain.ai.entity.AiJobStatus;
import com.mathfusion.domain.ai.repository.AiJobRepository;
import com.mathfusion.domain.user.entity.User;
import com.mathfusion.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AiJobService {

    private final AiJobRepository aiJobRepository;
    private final UserRepository userRepository;
    private final AiJobWorker aiJobWorker;
    private final ObjectMapper objectMapper;

    public AiJobCreateResponse createJob(String email, ChatRequest req) throws Exception {
        List<String> downloadUrls = req.normalized();
        if (downloadUrls.isEmpty()) {
            throw new IllegalArgumentException("downloadUrl or downloadUrls is required");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + email));

        AiJob job = AiJob.builder()
                .status(AiJobStatus.PENDING)
                .downloadUrlsJson(objectMapper.writeValueAsString(downloadUrls))
                .s3Key(req.getS3Key())
                .user(user)
                .build();

        AiJob saved = aiJobRepository.save(job);
        aiJobWorker.process(saved.getId());

        return new AiJobCreateResponse(saved.getId(), saved.getStatus());
    }

    @Transactional(readOnly = true)
    public AiJobResponse getJob(String email, Long jobId) throws Exception {
        AiJob job = aiJobRepository.findByIdAndUserEmail(jobId, email)
                .orElseThrow(() -> new IllegalArgumentException("AI job not found: " + jobId));

        List<Map<String, String>> result = null;
        if (job.getResultJson() != null && !job.getResultJson().isBlank()) {
            result = objectMapper.readValue(
                    job.getResultJson(),
                    new TypeReference<List<Map<String, String>>>() {}
            );
        }

        return AiJobResponse.builder()
                .jobId(job.getId())
                .status(job.getStatus())
                .result(result)
                .errorMessage(job.getErrorMessage())
                .createdAt(job.getCreatedAt())
                .updatedAt(job.getUpdatedAt())
                .build();
    }
}
