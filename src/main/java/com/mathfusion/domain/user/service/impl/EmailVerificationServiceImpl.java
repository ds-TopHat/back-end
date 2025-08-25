package com.mathfusion.domain.user.service.impl;

import com.mathfusion.domain.user.dto.EmailVerificationResponse;
import com.mathfusion.domain.user.entity.EmailVerification;
import com.mathfusion.domain.user.exception.EmailErrorCode;
import com.mathfusion.domain.user.exception.EmailException;
import com.mathfusion.domain.user.repository.EmailVerificationRepository;
import com.mathfusion.domain.user.service.EmailVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailVerificationServiceImpl implements EmailVerificationService {
    private final EmailVerificationRepository emailVerificationRepository;
    //실제 이메일 발송 로직 추가
    private final JavaMailSender mailSender;

    @Override
    public EmailVerificationResponse.SendCode sendVerificationCode(String email) {
        // 이전 코드 중 만료되지 않은 최근 코드가 있으면 삭제
        emailVerificationRepository.findTopByEmailOrderByExpiredTimeDesc(email)
                .ifPresent(emailVerification -> {
                    if (!emailVerification.isVerified() && emailVerification.getExpiredTime().isAfter(LocalDateTime.now())) {
                        emailVerificationRepository.delete(emailVerification);
                    }
                });

        String code = UUID.randomUUID().toString().substring(0, 6);

        EmailVerification emailVerification = EmailVerification.builder()
                .email(email)
                .code(code)
                .expiredTime(LocalDateTime.now().plusMinutes(5))
                .verified(false)
                .build();

        emailVerificationRepository.save(emailVerification);

        // 이메일 발송
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("회원가입 인증번호");
            message.setText("인증번호: " + code + "\n유효시간: 5분");
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("이메일 발송 실패: " + e.getMessage());
            return EmailVerificationResponse.SendCode.builder()
                    .success(false)
                    .message("이메일 전송 실패")
                    .build();
        }

        return EmailVerificationResponse.SendCode.builder()
                .success(true)
                .message("인증번호 전송 완료")
                .build();
    }

    @Override
    public EmailVerificationResponse.VerifyCode verifyCode(String email, String code) {
        // 최신 코드 기준으로 검증
        EmailVerification emailVerification = emailVerificationRepository.findTopByEmailOrderByExpiredTimeDesc(email)
                .filter(ev -> ev.getCode().equals(code))
                .orElseThrow(() -> new EmailException(EmailErrorCode.INVALID_CODE));

        if (emailVerification.isVerified()) {
            throw new EmailException(EmailErrorCode.ALREADY_VERIFIED);
        }

        if (emailVerification.getExpiredTime().isBefore(LocalDateTime.now())) {
            throw new EmailException(EmailErrorCode.EXPIRED);
        }

        emailVerification.setVerified(true);
        emailVerificationRepository.save(emailVerification);

        return EmailVerificationResponse.VerifyCode.builder()
                .success(true)
                .message("인증 성공")
                .build();
    }

}
