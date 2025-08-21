package com.mathfusion.domain.ai.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ChatRequest {
    private String downloadUrl;
    private List<String> downloadUrls;

    public List<String> normalized() {
        if (downloadUrls != null && !downloadUrls.isEmpty()) return downloadUrls;
        if (downloadUrl != null && !downloadUrl.isBlank())   return List.of(downloadUrl);
        return List.of();
    }
}
