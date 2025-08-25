package com.mathfusion.domain.user.service.impl;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.mathfusion.domain.user.dto.UserRequest;
import com.mathfusion.domain.user.entity.User;
import com.mathfusion.domain.user.exception.UserException;
import com.mathfusion.domain.user.repository.EmailVerificationRepository;
import com.mathfusion.domain.user.repository.UserRepository;
import com.mathfusion.domain.user.service.UserService;
import com.mathfusion.global.apiPayload.code.status.ErrorStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final EmailVerificationRepository emailVerificationRepository;

    @Override
    public Long signup(UserRequest.SignupRequest dto) {
        String email = dto.getEmail();
        log.info("[Signup] 시도: {}", email);

        if (email == null || email.trim().isEmpty() || dto.getPassword() == null || dto.getPassword().trim().isEmpty()) {
            log.error("[Signup] 이메일 또는 비밀번호 비어있음");
            throw new UserException(ErrorStatus.INVALID_INPUT);
        }

        if (userRepository.existsByEmail(email)) {
            log.error("[Signup] 이미 존재하는 이메일: {}", email);
            throw new UserException(ErrorStatus.USER_ALREADY_EXISTS);
        }

        User user = User.builder()
                .email(email)
                .password(bCryptPasswordEncoder.encode(dto.getPassword()))
                .build();

        User savedUser = userRepository.save(user);
        log.info("[Signup] 회원가입 완료: {}", savedUser.getEmail());
        return savedUser.getId();
    }

    @Override
    public void deleteByEmail(String email) {
        log.info("[Delete] 회원탈퇴 시도: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException(ErrorStatus.USER_NOT_FOUND));

        userRepository.delete(user);
        log.info("[Delete] 회원탈퇴 완료: {}", email);
    }
}