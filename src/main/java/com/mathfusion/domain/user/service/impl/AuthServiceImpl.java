package com.mathfusion.domain.user.service.impl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.mathfusion.domain.user.dto.UserResponse;
import com.mathfusion.domain.user.entity.RefreshToken;
import com.mathfusion.domain.user.entity.User;
import com.mathfusion.domain.user.exception.JwtErrorCode;
import com.mathfusion.domain.user.exception.JwtException;
import com.mathfusion.domain.user.exception.RefreshTokenErrorCode;
import com.mathfusion.domain.user.exception.RefreshTokenException;
import com.mathfusion.domain.user.exception.UserException;
import com.mathfusion.domain.user.repository.RefreshTokenRepository;
import com.mathfusion.domain.user.repository.UserRepository;
import com.mathfusion.domain.user.security.JwtUtil;
import com.mathfusion.domain.user.service.AuthService;
import com.mathfusion.global.apiPayload.code.status.ErrorStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public UserResponse.LoginResponse login(String email, String password) {
        log.info("[Login] 시도: {}", email);

        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            log.error("[Login] 이메일 또는 비밀번호 비어있음");
            throw new UserException(ErrorStatus.INVALID_INPUT);
        }

        // 인증
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );

            if (authentication == null || !authentication.isAuthenticated()) {
                log.error("[Login] 인증 실패: {}", email);
                throw new UserException(ErrorStatus.INVALID_INPUT);
            }

            log.info("[Login] 인증 성공: {}", email);

        } catch (Exception e) {
            log.error("[Login] 인증 중 오류: {}", e.getMessage(), e);
            throw new UserException(ErrorStatus.INVALID_INPUT);
        }

        // 유저 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("[Login] 사용자 없음: {}", email);
                    return new UserException(ErrorStatus.USER_NOT_FOUND);
                });

        // 토큰 생성
        String accessToken;
        String refreshToken;
        try {
            accessToken = jwtUtil.generateToken(user.getEmail());
            refreshToken = jwtUtil.generateRefreshToken(user.getEmail());
            log.info("[Login] 토큰 생성 성공");
        } catch (Exception e) {
            log.error("[Login] 토큰 생성 오류: {}", e.getMessage(), e);
            throw new JwtException(JwtErrorCode.INVALID_TOKEN);
        }

        // Refresh Token 저장
        try {
            refreshTokenRepository.findByUserId(user.getId())
                    .ifPresent(refreshTokenRepository::delete);

            refreshTokenRepository.save(RefreshToken.builder()
                    .userId(user.getId())
                    .token(refreshToken)
                    .build());

            log.info("[Login] Refresh Token 저장 완료");
        } catch (Exception e) {
            log.error("[Login] Refresh Token 저장 오류: {}", e.getMessage(), e);
            throw new RefreshTokenException(RefreshTokenErrorCode.INVALID);
        }

        return UserResponse.LoginResponse.builder()
                .email(user.getEmail())
                .message("로그인 성공")
                .token(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public String reissue(String refreshToken) {
        log.info("[Reissue] 토큰 재발급 시도");

        if (refreshToken == null || refreshToken.trim().isEmpty()) {
            log.error("[Reissue] Refresh Token 비어있음");
            throw new JwtException(JwtErrorCode.EMPTY_TOKEN);
        }

        try {
            if (!jwtUtil.validateToken(refreshToken)) {
                log.error("[Reissue] 유효하지 않은 Refresh Token");
                throw new JwtException(JwtErrorCode.INVALID_TOKEN);
            }

            String email = jwtUtil.getEmailFromToken(refreshToken);
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UserException(ErrorStatus.USER_NOT_FOUND));

            RefreshToken savedToken = refreshTokenRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new RefreshTokenException(RefreshTokenErrorCode.NOT_FOUND));

            if (!savedToken.getToken().equals(refreshToken)) {
                log.error("[Reissue] Refresh Token 불일치");
                throw new RefreshTokenException(RefreshTokenErrorCode.MISMATCH);
            }

            String newAccessToken = jwtUtil.generateToken(user.getEmail());
            log.info("[Reissue] 새 Access Token 발급 성공");
            return newAccessToken;

        } catch (Exception e) {
            log.error("[Reissue] 토큰 재발급 중 오류: {}", e.getMessage(), e);
            throw new JwtException(JwtErrorCode.INVALID_TOKEN);
        }
    }
}