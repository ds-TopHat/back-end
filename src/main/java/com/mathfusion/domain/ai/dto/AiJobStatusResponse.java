package com.mathfusion.domain.ai.dto;

import com.mathfusion.domain.ai.entity.AiJob;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AiJobStatusResponse {

    private final Long jobId;
    private final String status;
    private final String resultJson;
    private final String errorMessage;
    private final int retryCount;
    private final LocalDateTime createdAt;
    private final LocalDateTime startedAt;
    private final LocalDateTime finishedAt;

    public AiJobStatusResponse(AiJob job) {
        this.jobId = job.getId();
        this.status = job.getStatus().name();
        this.resultJson = job.getResultJson();
        this.errorMessage = job.getErrorMessage();
        this.retryCount = job.getRetryCount();
        this.createdAt = job.getCreatedAt();
        this.startedAt = job.getStartedAt();
        this.finishedAt = job.getFinishedAt();
    }
}