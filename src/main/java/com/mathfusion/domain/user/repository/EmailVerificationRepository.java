package com.mathfusion.domain.user.repository;

import com.mathfusion.domain.user.entity.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {
    Optional<EmailVerification> findByEmailAndCode(String email, String code);

    //이메일로 최신 인증 기록 가져오기
    Optional<EmailVerification> findTopByEmailOrderByExpiredTimeDesc(String email);
}
