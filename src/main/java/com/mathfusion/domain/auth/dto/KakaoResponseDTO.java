package com.mathfusion.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class KakaoResponseDTO {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class KakaoLoginResponseDTO{
        private String access_token;
        private String refresh_token;
        private String email;
        private String name;
        private String socialId;   // 카카오 id
        private Boolean isNew; // if true, need new signup logic
    }
}
