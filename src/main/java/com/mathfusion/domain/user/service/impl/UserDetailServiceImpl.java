package com.mathfusion.domain.user.service.impl;

import com.mathfusion.domain.user.exception.UserException;
import com.mathfusion.domain.user.repository.UserRepository;
import com.mathfusion.domain.user.service.UserDetailService;
import com.mathfusion.global.apiPayload.code.status.ErrorStatus;
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
    public UserDetails loadUserByUsername(String identifier) {
        // identifier가 숫자면 userId로, 아니면 email로 처리
        if (identifier.matches("\\d+")) {
            Long id = Long.parseLong(identifier);
            return userRepository.findById(id)
                    .orElseThrow(() -> new UserException(ErrorStatus.USER_NOT_FOUND));
        } else {
            return userRepository.findByEmail(identifier)
                    .orElseThrow(() -> new UserException(ErrorStatus.USER_NOT_FOUND));
        }
    }
}