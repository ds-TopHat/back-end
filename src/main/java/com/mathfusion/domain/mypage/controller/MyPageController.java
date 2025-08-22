package com.mathfusion.domain.mypage.controller;

import com.mathfusion.domain.mypage.dto.MyPageResponse;
import com.mathfusion.domain.mypage.service.MyPageService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v0/users/me")
public class MyPageController {

    private final MyPageService mypageService;

    @Operation(summary = "마이페이지 조회", description = "unitsList로 단원명을 많이 질문한 순으로 정렬해서 보내줍니다. 맨 위 단원 2개를 ~와 ~에 대해서 가장 많이 물어봤어요!에 퍼블리싱 하면 될 듯!")
    @GetMapping
    public ResponseEntity<MyPageResponse> getMypage(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        return ResponseEntity.ok(mypageService.getMypage(email));
    }
}
