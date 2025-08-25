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
<<<<<<< Updated upstream
import lombok.extern.slf4j.Slf4j;
=======
>>>>>>> Stashed changes

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
        log.info("로그인 시도: {}", email);
        log.info("입력된 비밀번호 길이: {}", password != null ? password.length() : "null");

        // 입력값 검증
        if (email == null || email.trim().isEmpty()) {
            log.error("이메일이 비어있습니다.");
            throw new UserException(ErrorStatus.INVALID_INPUT);
        }
        if (password == null || password.trim().isEmpty()) {
            log.error("비밀번호가 비어있습니다.");
            throw new UserException(ErrorStatus.INVALID_INPUT);
        }

        // 1. 인증
        try {
            log.info("AuthenticationManager.authenticate() 호출 시작");
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
            log.info("AuthenticationManager.authenticate() 호출 완료");

            // 인증 성공 시 authentication 객체를 사용하거나 검증
            if (authentication == null || !authentication.isAuthenticated()) {
                log.error("인증 실패: authentication이 null이거나 인증되지 않음");
                throw new UserException(ErrorStatus.INVALID_INPUT);
            }
            log.info("인증 성공: {}", email);
        } catch (org.springframework.security.authentication.BadCredentialsException e) {
            log.error("잘못된 비밀번호: {}", email);
            throw new UserException(ErrorStatus.INVALID_INPUT);
        } catch (org.springframework.security.authentication.DisabledException e) {
            log.error("비활성화된 계정: {}", email);
            throw new UserException(ErrorStatus.INVALID_INPUT);
        } catch (org.springframework.security.authentication.LockedException e) {
            log.error("잠긴 계정: {}", email);
            throw new UserException(ErrorStatus.INVALID_INPUT);
        } catch (org.springframework.security.authentication.AuthenticationServiceException e) {
            log.error("인증 서비스 오류: {}", e.getMessage(), e);
            throw new UserException(ErrorStatus.INVALID_INPUT);
        } catch (Exception e) {
<<<<<<< Updated upstream
            log.error("인증 중 예상치 못한 오류 발생: {} (타입: {})", e.getMessage(), e.getClass().getSimpleName(), e);
=======
>>>>>>> Stashed changes
            throw new UserException(ErrorStatus.INVALID_INPUT);
        }

        // 2. 유저 조회
<<<<<<< Updated upstream
        User user;
        try {
            user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UserException(ErrorStatus.USER_NOT_FOUND));
            log.info("유저 조회 성공: {}", user.getEmail());
        } catch (UserException e) {
            log.error("사용자를 찾을 수 없음: {}", email);
            throw e;
        } catch (Exception e) {
            log.error("유저 조회 중 데이터베이스 오류: {}", e.getMessage(), e);
            throw new UserException(ErrorStatus.USER_NOT_FOUND);
        }
=======
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException(ErrorStatus.USER_NOT_FOUND));
>>>>>>> Stashed changes

        // 3. 토큰 생성
        String accessToken;
        String refreshToken;
        try {
            accessToken = jwtUtil.generateToken(user.getEmail());
            refreshToken = jwtUtil.generateRefreshToken(user.getEmail());
            log.info("토큰 생성 성공");
        } catch (JwtException e) {
            log.error("JWT 토큰 생성 실패: {}", e.getMessage(), e);
            throw e;
        } catch (IllegalArgumentException e) {
            log.error("토큰 생성 중 잘못된 인수: {}", e.getMessage(), e);
            throw new JwtException(JwtErrorCode.INVALID_TOKEN);
        } catch (Exception e) {
            log.error("토큰 생성 중 예상치 못한 오류: {}", e.getMessage(), e);
            throw new JwtException(JwtErrorCode.INVALID_TOKEN);
        }

        // 4. Refresh Token 저장 (기존 있으면 삭제 후 새로 저장)
        try {
            // 기존 토큰 삭제
            refreshTokenRepository.findByUserId(user.getId())
                    .ifPresent(existingToken -> {
                        try {
                            refreshTokenRepository.delete(existingToken);
                            log.info("기존 Refresh Token 삭제 완료");
                        } catch (Exception e) {
                            log.warn("기존 Refresh Token 삭제 실패 (무시): {}", e.getMessage());
                        }
                    });

            // 새 토큰 저장
            RefreshToken entity = RefreshToken.builder()
                    .userId(user.getId())
                    .token(refreshToken)
                    .build();
            refreshTokenRepository.save(entity);
            log.info("새 Refresh Token 저장 완료");
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            log.error("Refresh Token 저장 중 데이터 무결성 오류: {}", e.getMessage(), e);
            throw new RefreshTokenException(RefreshTokenErrorCode.INVALID);
        } catch (Exception e) {
            log.error("Refresh Token 저장 중 예상치 못한 오류: {}", e.getMessage(), e);
            throw new RefreshTokenException(RefreshTokenErrorCode.INVALID);
        }

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
<<<<<<< Updated upstream
        log.info("토큰 재발급 시도");

        // 입력값 검증
        if (refreshToken == null || refreshToken.trim().isEmpty()) {
            log.error("Refresh Token이 비어있습니다.");
            throw new JwtException(JwtErrorCode.EMPTY_TOKEN);
        }

        try {
            // 1. 토큰 유효성 검사
            if (!jwtUtil.validateToken(refreshToken)) {
                log.error("유효하지 않은 Refresh Token");
                throw new JwtException(JwtErrorCode.INVALID_TOKEN);
            }

            // 2. 토큰에서 이메일 추출
            String email = jwtUtil.getEmailFromToken(refreshToken);
            log.info("토큰에서 이메일 추출 성공: {}", email);

            // 3. 사용자 조회
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UserException(ErrorStatus.USER_NOT_FOUND));
            log.info("사용자 조회 성공: {}", user.getEmail());

            // 4. DB 저장된 Refresh Token 조회
            RefreshToken savedToken = refreshTokenRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new RefreshTokenException(RefreshTokenErrorCode.NOT_FOUND));
            log.info("저장된 Refresh Token 조회 성공");

            // 5. 일치 여부 확인
            if (!savedToken.getToken().equals(refreshToken)) {
                log.error("Refresh Token 불일치");
                throw new RefreshTokenException(RefreshTokenErrorCode.MISMATCH);
            }

            // 6. 새 Access Token 발급
            String newAccessToken = jwtUtil.generateToken(user.getEmail());
            log.info("새 Access Token 발급 성공");
            return newAccessToken;

        } catch (JwtException e) {
            log.error("JWT 관련 오류: {}", e.getMessage());
            throw e;
        } catch (UserException e) {
            log.error("사용자 관련 오류: {}", e.getMessage());
            throw e;
        } catch (RefreshTokenException e) {
            log.error("Refresh Token 관련 오류: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("토큰 재발급 중 예상치 못한 오류: {}", e.getMessage(), e);
            throw new JwtException(JwtErrorCode.INVALID_TOKEN);
        }
=======
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
>>>>>>> Stashed changes
    }
}
