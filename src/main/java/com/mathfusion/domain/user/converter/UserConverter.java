package com.mathfusion.domain.user.converter;

import com.mathfusion.domain.auth.dto.KakaoRequestDTO;
import com.mathfusion.domain.user.entity.User;
import com.mathfusion.domain.user.entity.enums.UserStatus;
import org.springframework.stereotype.Component;

@Component
public class UserConverter {

    public static User toUser(KakaoRequestDTO.KakaoSignupRequestDTO request) {
        return User.builder()
                .email(request.getEmail())
                .name(request.getName())
                .socialId(request.getSocialId())
                .status(UserStatus.ACTIVE)
                .build();
    }
}
