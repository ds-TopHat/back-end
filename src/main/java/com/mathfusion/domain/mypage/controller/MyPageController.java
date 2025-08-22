package com.mathfusion.domain.mypage.controller;

import com.mathfusion.domain.mypage.dto.MyPageResponse;
import com.mathfusion.domain.mypage.service.MyPageService;
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

    @GetMapping
    public ResponseEntity<MyPageResponse> getMypage(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        return ResponseEntity.ok(mypageService.getMypage(email));
    }
}
