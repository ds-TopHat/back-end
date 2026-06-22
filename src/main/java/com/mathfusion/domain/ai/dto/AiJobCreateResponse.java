package com.mathfusion.domain.ai.dto;

import com.mathfusion.domain.ai.entity.AiJobStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AiJobCreateResponse {
    private Long jobId;
    private AiJobStatus status;
}
