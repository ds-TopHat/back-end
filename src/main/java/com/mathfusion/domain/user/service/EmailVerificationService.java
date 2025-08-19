package com.mathfusion.domain.user.service;

import com.mathfusion.domain.user.dto.EmailVerificationResponse;

public interface EmailVerificationService {
    EmailVerificationResponse.SendCode sendVerificationCode(String email);
    EmailVerificationResponse.VerifyCode verifyCode(String email, String code);
}

