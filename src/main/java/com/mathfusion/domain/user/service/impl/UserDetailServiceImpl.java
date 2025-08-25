package com.mathfusion.domain.user.service.impl;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.mathfusion.domain.user.entity.User;
import com.mathfusion.domain.user.exception.UserException;
import com.mathfusion.domain.user.repository.UserRepository;
import com.mathfusion.domain.user.service.UserDetailService;
import com.mathfusion.global.apiPayload.code.status.ErrorStatus;

import lombok.RequiredArgsConstructor;
<<<<<<< Updated upstream
import lombok.extern.slf4j.Slf4j;
=======
>>>>>>> Stashed changes

//인증 구현체
@RequiredArgsConstructor
@Service
@Slf4j
public class UserDetailServiceImpl implements UserDetailService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) {
        log.info("UserDetails 로드 시도: {}", email);
        
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UserException(ErrorStatus.USER_NOT_FOUND));

            log.info("UserDetails 로드 성공: {}", email);
            log.info("저장된 비밀번호 해시: {}", user.getPassword());
            log.info("저장된 비밀번호 길이: {}", user.getPassword().length());
            
            // User 엔티티가 이미 UserDetails를 구현하고 있으므로 직접 반환
            return user;
        } catch (UserException e) {
            log.error("사용자를 찾을 수 없음: {}", email);
            throw e;
        } catch (Exception e) {
            log.error("UserDetails 로드 중 예상치 못한 오류: {}", e.getMessage(), e);
            throw new UserException(ErrorStatus.USER_NOT_FOUND);
        }
    }
}