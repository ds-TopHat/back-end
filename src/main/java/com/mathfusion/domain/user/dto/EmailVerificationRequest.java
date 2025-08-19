package com.mathfusion.domain.user.dto;

import lombok.*;

public class EmailVerificationRequest {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SendCode {
        private String email;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class VerifyCode {
        private String email;
        private String code;
    }
}
