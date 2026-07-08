package com.mathfusion.domain.ai.exception;

import lombok.Getter;

@Getter
public class AiJobException extends RuntimeException {

    private final com.mathfusion.domain.ai.exception.AiJobErrorCode errorCode;

    public AiJobException(com.mathfusion.domain.ai.exception.AiJobErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public AiJobException(com.mathfusion.domain.ai.exception.AiJobErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }
}