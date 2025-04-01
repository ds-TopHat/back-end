package com.mathfusion.domain.user.dto;

import lombok.Getter;
import lombok.Setter;

// 회원 가입 구현
@Getter
@Setter
public class AddUserRequest {
    private String email;
    private String password;
}
