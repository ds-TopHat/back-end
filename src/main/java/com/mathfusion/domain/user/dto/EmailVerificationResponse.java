package com.mathfusion.domain.user.dto;

import lombok.*;

public class EmailVerificationResponse {

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SendCode {
        private boolean success;
        private String message;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class VerifyCode {
        private boolean success;
        private String message;
    }

}
