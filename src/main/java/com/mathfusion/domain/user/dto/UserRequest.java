package com.mathfusion.domain.user.dto;

import lombok.*;

public class UserRequest {
    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class SignupRequest {
        private String email;
        private String password;
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class LoginRequest {
        private String email;
        private String password;
    }

    // ===================== RefreshToken DTO =====================
    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class RefreshTokenRequest {
        private String refreshToken;
    }
}