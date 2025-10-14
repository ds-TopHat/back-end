package com.mathfusion.domain.auth.service;

import com.mathfusion.domain.auth.dto.KakaoRequestDTO;
import com.mathfusion.domain.auth.dto.KakaoResponseDTO;
import com.mathfusion.domain.auth.dto.KakaoUserInfo;
import com.mathfusion.domain.user.converter.UserConverter;
import com.mathfusion.domain.user.entity.RefreshToken;
import com.mathfusion.domain.user.entity.User;
import com.mathfusion.domain.user.entity.enums.LoginType;
import com.mathfusion.domain.user.exception.UserException;
import com.mathfusion.domain.user.repository.RefreshTokenRepository;
import com.mathfusion.domain.user.repository.UserRepository;
import com.mathfusion.domain.user.security.JwtUtil;
import com.mathfusion.global.apiPayload.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KakaoAuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;

    // 카카오 인가코드 처리(isnew 여부)
    public KakaoResponseDTO.KakaoLoginResponseDTO processKakaoLogin(KakaoRequestDTO.KakaoAuthCodeRequestDTO request){
        String kakaoAccessToken = request.getAccessToken();
        KakaoUserInfo userInfo = getUserInfo(kakaoAccessToken);

        Optional<User> userOpt = userRepository.findBySocialIdAndLoginType(userInfo.getId(), LoginType.KAKAO);

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    user.getId(), null, Collections.emptyList()
            );

            Map<String, String> tokens = generateTokens(user);

            // DB에 Refresh Token 저장
            refreshTokenRepository.findByUserId(user.getId())
                    .ifPresent(refreshTokenRepository::delete);

            refreshTokenRepository.save(RefreshToken.builder()
                    .userId(user.getId())
                    .token(tokens.get("refresh_token"))
                    .build());

            return KakaoResponseDTO.KakaoLoginResponseDTO.builder()
                    .access_token(tokens.get("access_token"))
                    .refresh_token(tokens.get("refresh_token"))
                    .email(user.getEmail())
                    .socialId(user.getSocialId())
                    .isNew(false)
                    .build();
        } else {
            // 신규 유저 → DB에 추가 후 refresh token 저장
            User newUser = userRepository.save(User.builder()
                    .email(userInfo.getEmail())
                    .socialId(userInfo.getId())
                    .loginType(LoginType.KAKAO)
                    .build());

            String accessToken = jwtUtil.generateToken(newUser.getEmail());
            String refreshToken = jwtUtil.generateRefreshToken(newUser.getEmail());

            refreshTokenRepository.save(RefreshToken.builder()
                    .userId(newUser.getId())
                    .token(refreshToken)
                    .build());

            return KakaoResponseDTO.KakaoLoginResponseDTO.builder()
                    .access_token(accessToken)
                    .refresh_token(refreshToken)
                    .email(newUser.getEmail())
                    .socialId(newUser.getSocialId())
                    .isNew(true)
                    .build();
        }
    }

    // 카카오 회원가입
    public KakaoResponseDTO.KakaoLoginResponseDTO signupKakaoMember(KakaoRequestDTO.KakaoSignupRequestDTO request){

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserException(ErrorStatus.ALREADY_REGISTERED_USER);
        }

        User user = UserConverter.toUser(request);
        User savedUser = userRepository.save(user);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                savedUser.getId(),
                null,
                Collections.emptyList()
        );

        String accessToken = jwtUtil.generateToken(savedUser.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(savedUser.getEmail());

        refreshTokenRepository.findByUserId(user.getId())
                .ifPresent(refreshTokenRepository::delete);

        refreshTokenRepository.save(RefreshToken.builder()
                .userId(user.getId())
                .token(refreshToken)
                .build());

        return KakaoResponseDTO.KakaoLoginResponseDTO.builder()
                .access_token(accessToken)
                .refresh_token(refreshToken)
                .email(user.getEmail())
                .socialId(user.getSocialId())
                .isNew(false)
                .build();
    }

    // 카카오에서 발급받은 access 토큰을 이용해서 사용자 정보 요청하는 메서드
    private KakaoUserInfo getUserInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.GET,
                request,
                Map.class
        );

        Map<String, Object> body = response.getBody();
        if (body == null) throw new RuntimeException("카카오 응답이 비어있습니다.");

        Map<String, Object> kakaoAccount = (Map<String, Object>) body.getOrDefault("kakao_account", Collections.emptyMap());
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.getOrDefault("profile", Collections.emptyMap());

        String email = (String) kakaoAccount.get("email"); // null 가능
        String id = String.valueOf(body.get("id"));

        return new KakaoUserInfo(email, id);
    }

    // 카카오 서비스 로그인
    public KakaoResponseDTO.KakaoLoginResponseDTO loginKakaoMember(KakaoRequestDTO.KakaoLoginRequestDTO request) {
        Optional<User> userOpt = userRepository.findBySocialIdAndLoginType(request.getSocialId(), LoginType.KAKAO);

        if (userOpt.isEmpty()) {
            throw new RuntimeException("가입되지 않은 회원입니다.");
        }

        User user = userOpt.get();

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getId(), null, Collections.emptyList()
        );

        Map<String, String> tokens = generateTokens(user);

        return KakaoResponseDTO.KakaoLoginResponseDTO.builder()
                .access_token(tokens.get("access_token"))
                .refresh_token(tokens.get("refresh_token"))
                .email(user.getEmail())
                .socialId(user.getSocialId())
                .isNew(false)
                .loginType(LoginType.KAKAO)
                .build();
    }

    private Map<String, String> generateTokens(User user) {
        String jwtAccessToken = jwtUtil.generateToken(user.getEmail());
        String jwtRefreshToken = jwtUtil.generateRefreshToken(user.getEmail());

        return Map.of(
                "access_token", jwtAccessToken,
                "refresh_token", jwtRefreshToken
        );
    }
}

