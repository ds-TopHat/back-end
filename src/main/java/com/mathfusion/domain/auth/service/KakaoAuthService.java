package com.mathfusion.domain.auth.service;

import com.mathfusion.domain.auth.dto.KakaoRequestDTO;
import com.mathfusion.domain.auth.dto.KakaoResponseDTO;
import com.mathfusion.domain.auth.dto.KakaoUserInfo;
import com.mathfusion.domain.user.converter.UserConverter;
import com.mathfusion.domain.user.entity.User;
import com.mathfusion.domain.user.entity.enums.LoginType;
import com.mathfusion.domain.user.entity.enums.UserStatus;
import com.mathfusion.domain.user.repository.SocialUserRepository;
import com.mathfusion.domain.user.repository.UserRepository;
import com.mathfusion.domain.user.security.TokenProvider;
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
    private final SocialUserRepository socialUserRepository;
    private final TokenProvider tokenProvider;

    // 카카오 로그인 요청 처리 서비스
    public KakaoResponseDTO.KakaoLoginResponseDTO processKakaoLogin(KakaoRequestDTO.KakaoLoginRequestDTO request){
        String kakaoAccessToken = request.getAccessToken();
        KakaoUserInfo userInfo = getUserInfo(kakaoAccessToken);

        Optional<User> user = socialUserRepository.findBySocialIdAndLoginType(userInfo.getId(), LoginType.KAKAO);

        // user 에 객체가 들어있는지 확인
        if (user.isPresent()) {

            if (user.get().getStatus() == UserStatus.INACTIVE) {
                throw new RuntimeException("탈퇴한 회원입니다.");
            }

            // 이미 가입한 유저라면 로그인 완료 처리
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    user.get().getId(),
                    null,
                    Collections.emptyList()
            );

            // authentication 으로 access, refresh 토큰 발급
            String jwtAccessToken = tokenProvider.createToken(authentication.getName());
            String jwtRefreshToken = tokenProvider.createRefreshToken(authentication.getName());

            return KakaoResponseDTO.KakaoLoginResponseDTO.builder()
                    .access_token(jwtAccessToken)
                    .refresh_token(jwtRefreshToken)
                    .email(user.get().getEmail())
                    .name(user.get().getName())  // DB 저장된 이름 기준
                    .socialId(user.get().getSocialId())
                    .isNew(false)
                    .build();
        }

        // 기존 가입 정보가 없는 경우 → 추가 정보 필요
        else {
            return KakaoResponseDTO.KakaoLoginResponseDTO.builder()
                    .email(userInfo.getEmail())
                    .name(userInfo.getNickname())   // 카카오에서 내려온 닉네임
                    .socialId(userInfo.getId()) // 카카오에서 내려온 ID
                    .isNew(true)
                    .build();
        }
    }

    // 카카오 회원가입 서비스
    public KakaoResponseDTO.KakaoLoginResponseDTO signupKakaoMember(KakaoRequestDTO.KakaoSignupRequestDTO request){

        if(userRepository.existsByEmail(request.getEmail())){
            throw new RuntimeException("이미 일반 회원가입을 완료한 사용자입니다.");
        }

        User user = UserConverter.toUser(request);
        User savedUser = userRepository.save(user);

        // 소셜 회원 가입 완료 후 로그인 처리
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getId(),
                null,
                Collections.emptyList()
        );

        //authentication 으로 access, refresh 토큰 발급
        String token = tokenProvider.createToken(authentication.getName());
        String refreshToken = tokenProvider.createRefreshToken(authentication.getName());

        return KakaoResponseDTO.KakaoLoginResponseDTO.builder()
                .access_token(token)
                .refresh_token(refreshToken)
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
        Map<String, Object> kakaoAccount = (Map<String, Object>) body.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        return new KakaoUserInfo(
                (String) kakaoAccount.get("email"),
                (String) profile.get("nickname"),
                String.valueOf(body.get("id"))
        );
    }
}
