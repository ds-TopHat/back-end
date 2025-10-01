package com.mathfusion.domain.auth.dto;

import com.mathfusion.domain.user.entity.enums.LoginType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

public class KakaoRequestDTO {

    // 인가코드
    @Getter
    @Setter
    public static class  KakaoAuthCodeRequestDTO {
        @NotBlank(message = "access token은 필수입니다.")
        private String accessToken;
    }

    // 로그인
    @Getter
    @Setter
    public static class KakaoLoginRequestDTO {
        @NotBlank(message = "소셜 id는 필수입니다.")
        private String socialId;   // DB 조회용
        @NotNull(message = "로그인 타입은 필수입니다.")
        private LoginType loginType; // KAKAO 고정
    }

    // 회원가입
    @Getter
    @Setter
    public static class KakaoSignupRequestDTO{
        @Email(message = "올바른 이메일 형식이어야 합니다.")
        private String email;

        @NotBlank(message = "소셜 id는 필수입니다.")
        private String socialId;

        @NotBlank(message = "이름은 필수입니다.")
        private String name;

        private Boolean marketingAgree;
    }
}
