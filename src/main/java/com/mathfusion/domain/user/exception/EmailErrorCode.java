package com.mathfusion.domain.user.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EmailErrorCode {
    NOT_FOUND("EV001", "해당 이메일로 요청된 인증이 없습니다."),
    INVALID_CODE("EV002", "인증번호가 올바르지 않습니다."),
    EXPIRED("EV003", "인증번호가 만료되었습니다."),
    ALREADY_VERIFIED("EV004", "이미 인증된 이메일입니다.");

    private final String code;
    private final String message;
}
