package com.mathfusion.domain.user.service.impl;

import com.mathfusion.global.apiPayload.code.status.ErrorStatus;
import com.mathfusion.domain.user.entity.User;
import com.mathfusion.domain.user.exception.UserException;
import com.mathfusion.domain.user.repository.UserRepository;
import com.mathfusion.domain.user.service.UserDetailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

//인증 구현체
@RequiredArgsConstructor
@Service
@Slf4j
public class UserDetailServiceImpl implements UserDetailService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) {
        log.info("[UserDetails] 로드 시도: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("[UserDetails] 사용자 없음: {}", email);
                    return new UserException(ErrorStatus.USER_NOT_FOUND);
                });

        log.info("[UserDetails] 로드 성공: {} (비밀번호 길이: {})", email, user.getPassword().length());
        return user; // User 엔티티가 UserDetails를 구현하므로 직접 반환
    }
}