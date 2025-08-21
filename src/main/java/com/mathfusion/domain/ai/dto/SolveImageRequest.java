package com.mathfusion.domain.ai.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SolveImageRequest {
    // 1장 케이스
    private String downloadUrl;
    // 2장 케이스: [0]=문제, [1]=사용자 풀이
    private List<String> downloadUrls;
}
