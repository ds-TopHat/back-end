package com.mathfusion.domain.user.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum JwtErrorCode {
    INVALID_TOKEN("JWT001", "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN("JWT002", "만료된 토큰입니다."),
    UNSUPPORTED_TOKEN("JWT003", "지원되지 않는 토큰 형식입니다."),
    EMPTY_TOKEN("JWT004", "토큰이 존재하지 않습니다."),
    ACCESS_DENIED("JWT005", "접근 권한이 없습니다.");

    private final String code;
    private final String message;
}
