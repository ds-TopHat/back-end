package com.mathfusion.domain.user.exception;

import com.mathfusion.domain.exception.ErrorStatus;
import com.mathfusion.domain.exception.GeneralException;

public class UserException extends GeneralException {
    public UserException(ErrorStatus errorStatus){
        super(errorStatus);
    }
}
