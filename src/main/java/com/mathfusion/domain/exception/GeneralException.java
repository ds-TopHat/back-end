package com.mathfusion.domain.exception;
import com.mathfusion.domain.exception.ErrorStatus;

//공통 예외
//각 도메인별 예외가 상속받는 클래스
//Errorstatus 받아서 예외 만들 수 있도록


public class GeneralException extends RuntimeException {
    private final ErrorStatus errorStatus;

    public GeneralException(ErrorStatus errorStatus){
        super(errorStatus.getMessage());
        this.errorStatus = errorStatus;
    }

    public ErrorStatus getErrorStatus(){
        return errorStatus;
    }
}
