package com.mathfusion.domain.ai.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ChatRequest {
    private List<String> downloadUrls;
    private String s3Key;

    public List<String> normalized() {
        if (downloadUrls != null && !downloadUrls.isEmpty()) return downloadUrls;
        return List.of();
    }
}
