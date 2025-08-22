package com.mathfusion.domain.wrongnote.dto;

import lombok.*;

import java.time.LocalDateTime;

public class WrongNoteResponse {

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PUBLIC)
    @NoArgsConstructor(access = AccessLevel.PUBLIC)
    public static class WrongNoteListResponse {
        private Long questionId;
        private String problemImageUrl;
        private String unitType;
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PUBLIC)
    @NoArgsConstructor(access = AccessLevel.PUBLIC)
    public static class WrongNoteDetailResponse {
        private Long questionId;
        private Long unitId;
        private String unitType;
        private LocalDateTime createdAt;
        private String problemImageUrl;
        private String aiAnswer;
    }
}
