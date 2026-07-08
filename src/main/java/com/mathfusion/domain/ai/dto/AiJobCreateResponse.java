package com.mathfusion.domain.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AiJobCreateResponse {
    private Long jobId;
    private String status;
}