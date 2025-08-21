package com.mathfusion.global.s3.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PresignedPair {
    private String uploadUrl;
    private String downloadUrl;
}