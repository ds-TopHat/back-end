package com.mathfusion.domain.user.service.impl;

import com.mathfusion.global.apiPayload.code.status.ErrorStatus;
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
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public UserResponse.LoginResponse login(String email, String password) {
        // 1. 인증
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
        } catch (Exception e) {
            throw new UserException(ErrorStatus.INVALID_INPUT);
        }

        // 2. 유저 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException(ErrorStatus.USER_NOT_FOUND));

        // 3. 토큰 생성
        String accessToken = jwtUtil.generateToken(user.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

        // 4. Refresh Token 저장 (기존 있으면 삭제 후 새로 저장)
        refreshTokenRepository.findByUserId(user.getId())
                .ifPresent(refreshTokenRepository::delete);

        RefreshToken entity = RefreshToken.builder()
                .userId(user.getId())
                .token(refreshToken)
                .build();
        refreshTokenRepository.save(entity);

        // 5. 응답 반환
        return UserResponse.LoginResponse.builder()
                .email(user.getEmail())
                .message("로그인 성공")
                .token(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public String reissue(String refreshToken) {
        // 1. 토큰 유효성 검사
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new JwtException(JwtErrorCode.INVALID_TOKEN);
        }

        // 2. 토큰에서 이메일 추출
        String email = jwtUtil.getEmailFromToken(refreshToken);

        // 3. 사용자 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException(ErrorStatus.USER_NOT_FOUND));

        // 4. DB 저장된 Refresh Token 조회
        RefreshToken savedToken = refreshTokenRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RefreshTokenException(RefreshTokenErrorCode.NOT_FOUND));

        // 5. 일치 여부 확인
        if (!savedToken.getToken().equals(refreshToken)) {
            throw new RefreshTokenException(RefreshTokenErrorCode.MISMATCH);
        }

        // 6. 새 Access Token 발급
        return jwtUtil.generateToken(user.getEmail());
    }
}
