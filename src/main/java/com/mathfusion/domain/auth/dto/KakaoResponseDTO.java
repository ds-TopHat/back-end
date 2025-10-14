package com.mathfusion.domain.auth.dto;

import com.mathfusion.domain.user.entity.enums.LoginType;
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
        private String socialId;
        private Boolean isNew;
        private LoginType loginType;
        private String name;
    }
}
