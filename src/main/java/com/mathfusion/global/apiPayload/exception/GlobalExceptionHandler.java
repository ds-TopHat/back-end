package com.mathfusion.global.apiPayload.exception;

import com.mathfusion.global.apiPayload.code.status.ErrorStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(GeneralException e){
        ErrorStatus status = e.getErrorStatus();
        ErrorResponse response = new ErrorResponse(status.getCode(), status.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e){
        log.error("Unhandled exception occurred", e);
        ErrorResponse response = new ErrorResponse("5999", "알 수 없는 서버 오류가 발생했습니다.");
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(com.mathfusion.domain.ai.exception.AiJobException.class)
    public ResponseEntity<ErrorResponse> handleAiJobException(com.mathfusion.domain.ai.exception.AiJobException e) {
        com.mathfusion.domain.ai.exception.AiJobErrorCode errorCode = e.getErrorCode();
        ErrorResponse response = new ErrorResponse(errorCode.getCode(), errorCode.getMessage());
        return new ResponseEntity<>(response, errorCode.getHttpStatus());
    }

    //에러 응답
    public record ErrorResponse(String code, String message){}
}
