package com.mathfusion.domain.user.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RefreshTokenErrorCode {
    NOT_FOUND("RT001", "Refresh Token이 존재하지 않습니다."),
    INVALID("RT002", "유효하지 않은 Refresh Token입니다."),
    MISMATCH("RT003", "Refresh Token이 일치하지 않습니다."),
    USER_NOT_FOUND("RT004", "사용자를 찾을 수 없습니다.");

    private final String code;
    private final String message;
}