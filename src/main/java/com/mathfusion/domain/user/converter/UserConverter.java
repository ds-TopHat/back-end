package com.mathfusion.domain.user.converter;

import com.mathfusion.domain.auth.dto.KakaoRequestDTO;
import com.mathfusion.domain.user.entity.User;
import com.mathfusion.domain.user.entity.enums.LoginType;
import com.mathfusion.domain.user.entity.enums.UserStatus;
import org.springframework.stereotype.Component;

@Component
public class UserConverter {

    public static User toUser(KakaoRequestDTO.KakaoSignupRequestDTO request) {
        return User.builder()
                .email(request.getEmail())
                .socialId(request.getSocialId())
                .loginType(LoginType.KAKAO)
                .password("SOCIAL_LOGIN") // 소셜일 경우
                .status(UserStatus.ACTIVE)
                .build();
    }
}
