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
    public EmailVerificationResponse.SendCode sendVerificationCode(String email){
        String code = UUID.randomUUID().toString().substring(0,6); //랜덤 6자리 코드


        //db 저장
        EmailVerification emailVerification = EmailVerification.builder()
                .email(email)
                .code(code)
                .expiredTime(LocalDateTime.now().plusMinutes(5)) //만료 시간 5분
                .verified(false)
                .build();

        emailVerificationRepository.save(emailVerification);


        //실제 이메일 발송
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("회원가입 인증번호");
        message.setText("인증번호: " + code + "\n유효시간: 5분");
        mailSender.send(message);


        //이메일 전송 로직 추가
        System.out.println("인증번호: "+ code + " 를 " + email + " 로 전송");

        // DTO 반환
        return EmailVerificationResponse.SendCode.builder()
                .success(true)
                .message("인증번호 전송 완료")
                .build();
    }

    @Override
    public EmailVerificationResponse.VerifyCode verifyCode(String email, String code){
      EmailVerification emailVerification = emailVerificationRepository.findByEmailAndCode(email,code)
              .orElseThrow(()-> new EmailException(EmailErrorCode.INVALID_CODE));

      //예외처리
      if (emailVerification.isVerified()){
          throw new EmailException(EmailErrorCode.ALREADY_VERIFIED);
      }

      if (emailVerification.getExpiredTime().isBefore(LocalDateTime.now())){
          throw new EmailException(EmailErrorCode.EXPIRED);
      }

      //검증 완료 처리
        emailVerification.setVerified(true);
      emailVerificationRepository.save(emailVerification);

      return EmailVerificationResponse.VerifyCode.builder()
              .success(true)
              .message("인증 성공")
              .build();
    }
}
