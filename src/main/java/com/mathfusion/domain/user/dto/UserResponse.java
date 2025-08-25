package com.mathfusion.domain.user.dto;

import lombok.*;

public class UserResponse{
    //회원가입 로직
    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class SignupResponse {
        private Long id;
        private String email;
    }

    //로그인 로직
    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class LoginResponse {
        private String email;
        private String message;
        //탈퇴
        private String token;
        private String refreshToken;  // Refresh Token
    }
}
