package com.mathfusion.domain.ai.dto;

import com.mathfusion.domain.ai.entity.AiJobStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Builder
public class AiJobResponse {
    private Long jobId;
    private AiJobStatus status;
    private List<Map<String, String>> result;
    private String errorMessage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
