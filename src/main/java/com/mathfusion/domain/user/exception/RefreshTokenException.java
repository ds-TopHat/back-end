package com.mathfusion.domain.user.exception;

import lombok.Getter;

@Getter
public class RefreshTokenException extends RuntimeException {

    private final RefreshTokenErrorCode errorCode;

    public RefreshTokenException(RefreshTokenErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
