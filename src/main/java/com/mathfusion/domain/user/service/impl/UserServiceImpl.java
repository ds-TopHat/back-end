package com.mathfusion.domain.user.service.impl;

import com.mathfusion.domain.user.dto.UserRequest;
import com.mathfusion.domain.user.entity.EmailVerification;
import com.mathfusion.domain.user.entity.User;
import com.mathfusion.domain.user.entity.enums.LoginType;
import com.mathfusion.domain.user.entity.enums.UserStatus;
import com.mathfusion.domain.user.exception.EmailErrorCode;
import com.mathfusion.domain.user.exception.EmailException;
import com.mathfusion.domain.user.exception.UserException;
import com.mathfusion.domain.user.repository.EmailVerificationRepository;
import com.mathfusion.domain.user.repository.UserRepository;
import com.mathfusion.domain.user.service.UserService;
import com.mathfusion.global.apiPayload.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final EmailVerificationRepository emailVerificationRepository;

    @Override
    @Transactional
    public Long signup(UserRequest.SignupRequest dto) {
        String email = dto.getEmail();

        // 최신 이메일 인증 기록 가져오기
        EmailVerification emailVerification = emailVerificationRepository.findTopByEmailOrderByExpiredTimeDesc(email)
                .orElseThrow(() -> new EmailException(EmailErrorCode.NOT_VERIFIED));

        if (!emailVerification.isVerified()) {
            throw new EmailException(EmailErrorCode.NOT_VERIFIED);
        }

        // 이미 가입된 계정인지 확인
        if (userRepository.existsByEmail(email)) {
            throw new UserException(ErrorStatus.USER_ALREADY_EXISTS);
        }

        User user = User.builder()
                .email(email)
                .password(bCryptPasswordEncoder.encode(dto.getPassword()))
                .loginType(LoginType.NORMAL)
                .status(UserStatus.ACTIVE)
                .build();

        try {
            User savedUser = userRepository.save(user);
            log.info("[Signup] 회원가입 완료: {}", savedUser.getEmail());
            return savedUser.getId();
        } catch (Exception e) {
            log.error("[Signup] 회원가입 중 예기치 못한 오류: {}", e.getMessage(), e);
            throw new UserException(ErrorStatus.INTERNAL_ERROR);
        }
    }



    @Override
    @Transactional
    public void deleteByEmail(String identifier) {
        User user = identifier.matches("\\d+")
                ? userRepository.findById(Long.parseLong(identifier))
                .orElseThrow(() -> new UserException(ErrorStatus.USER_NOT_FOUND))
                : userRepository.findByEmail(identifier)
                .orElseThrow(() -> new UserException(ErrorStatus.USER_NOT_FOUND));

        user.setStatus(UserStatus.INACTIVE);
        userRepository.save(user);
    }
}