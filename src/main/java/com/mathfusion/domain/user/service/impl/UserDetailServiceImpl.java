package com.mathfusion.domain.user.service.impl;

import org.springframework.stereotype.Service;

import com.mathfusion.domain.user.entity.User;
import com.mathfusion.domain.user.exception.UserException;
import com.mathfusion.domain.user.repository.UserRepository;
import com.mathfusion.domain.user.service.UserDetailService;
import com.mathfusion.global.apiPayload.code.status.ErrorStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailServiceImpl implements UserDetailService {

    private final UserRepository userRepository;

    @Override
    public User loadUserByUsername(String email) {
        log.info("[UserDetails] 로드 시도: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("[UserDetails] 사용자 없음: {}", email);
                    return new UserException(ErrorStatus.USER_NOT_FOUND);
                });

        log.info("[UserDetails] 로드 성공: {}", email);
        return user;
    }
}