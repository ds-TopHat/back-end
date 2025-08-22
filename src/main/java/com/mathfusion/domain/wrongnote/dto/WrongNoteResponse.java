package com.mathfusion.domain.wrongnote.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class WrongNoteResponse {
    private Long questionId;
    private String problemImageUrl;
    private String unitType;
}
