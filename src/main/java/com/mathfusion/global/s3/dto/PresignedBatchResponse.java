package com.mathfusion.global.s3.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PresignedBatchResponse {
    private List<String> uploadUrls;
    private List<String> downloadUrls;
}