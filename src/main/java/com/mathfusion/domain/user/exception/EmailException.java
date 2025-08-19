package com.mathfusion.domain.user.exception;


public class EmailException extends RuntimeException {

    private final EmailErrorCode errorCode;

    public EmailException(EmailErrorCode errorCode){
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public EmailErrorCode getErrorCode(){
        return errorCode;
    }
}
