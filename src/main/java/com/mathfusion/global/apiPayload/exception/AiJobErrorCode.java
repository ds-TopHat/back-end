package com.mathfusion.domain.ai.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum AiJobErrorCode {

    AI_JOB_REQUEST_EMPTY(HttpStatus.BAD_REQUEST, "AI_JOB_REQUEST_EMPTY", "요청 본문이 비어 있습니다."),
    AI_JOB_IMAGE_URL_REQUIRED(HttpStatus.BAD_REQUEST, "AI_JOB_IMAGE_URL_REQUIRED", "downloadUrl 또는 downloadUrls 중 하나는 반드시 필요합니다."),
    AI_JOB_INVALID_ID(HttpStatus.BAD_REQUEST, "AI_JOB_INVALID_ID", "jobId는 1 이상의 값이어야 합니다."),
    AI_JOB_NOT_FOUND(HttpStatus.NOT_FOUND, "AI_JOB_NOT_FOUND", "AI Job을 찾을 수 없습니다."),
    AI_JOB_ACCESS_DENIED(HttpStatus.FORBIDDEN, "AI_JOB_ACCESS_DENIED", "해당 AI Job에 접근할 권한이 없습니다."),
    AI_JOB_CREATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "AI_JOB_CREATE_FAILED", "AI Job 생성에 실패했습니다."),
    AI_JOB_READ_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "AI_JOB_READ_FAILED", "AI Job 조회에 실패했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    AiJobErrorCode(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}