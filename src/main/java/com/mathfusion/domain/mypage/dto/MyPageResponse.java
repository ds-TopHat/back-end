package com.mathfusion.domain.mypage.dto;

import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class MyPageResponse {
    private String name;
    private List<Units> unitsList;

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PUBLIC)
    @NoArgsConstructor(access = AccessLevel.PUBLIC)
    public static class Units {
        private Long id;
        private String type;
    }
}
