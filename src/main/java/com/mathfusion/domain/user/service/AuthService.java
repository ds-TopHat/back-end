package com.mathfusion.domain.user.service;

import com.mathfusion.domain.user.dto.UserResponse;

public interface AuthService {
    UserResponse.LoginResponse login(String email, String password);
    UserResponse.LoginResponse reissue(String refreshToken); // 토큰 재발급
}
