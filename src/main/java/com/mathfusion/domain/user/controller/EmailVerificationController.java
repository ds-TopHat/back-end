package com.mathfusion.domain.user.controller;

import com.mathfusion.domain.user.dto.EmailVerificationRequest;
import com.mathfusion.domain.user.dto.EmailVerificationResponse;
import com.mathfusion.domain.user.service.EmailVerificationService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v0/email-auth")
public class EmailVerificationController {

    private final EmailVerificationService service;

    @Operation(summary = "회원가입용 인증번호 이메일 전송")
    @PostMapping("/request-code")
    public ResponseEntity<?> sendCode(
            @RequestBody @Validated EmailVerificationRequest.SendCode request){
        service.sendVerificationCode(request.getEmail());

        EmailVerificationResponse.SendCode response = EmailVerificationResponse.SendCode.builder()
                .success(true)
                .message("인증번호 전송 완료")
                .build();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "회원가입용 인증번호 이메일 검증")
    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyCode(
            @RequestBody @Validated EmailVerificationRequest.VerifyCode request){
        service.verifyCode(request.getEmail(), request.getCode());

        EmailVerificationResponse.VerifyCode response = EmailVerificationResponse.VerifyCode.builder()
                .success(true)
                .message("인증번호 검증 완료")
                .build();

        return ResponseEntity.ok(response);

    }
}
