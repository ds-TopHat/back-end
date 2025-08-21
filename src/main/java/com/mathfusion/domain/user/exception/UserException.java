package com.mathfusion.domain.user.exception;

import com.mathfusion.global.apiPayload.code.status.ErrorStatus;
import com.mathfusion.global.apiPayload.exception.GeneralException;

public class UserException extends GeneralException {
    public UserException(ErrorStatus errorStatus){
        super(errorStatus);
    }
}
