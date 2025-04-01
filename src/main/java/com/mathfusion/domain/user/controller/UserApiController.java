package com.mathfusion.domain.user.controller;

import com.mathfusion.domain.user.dto.AddUserRequest;
import com.mathfusion.domain.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@RequiredArgsConstructor
@Controller
public class UserApiController {

    private final UserService userService;
    // 로그인 메서드
    @PostMapping("/user")
    public String signup(AddUserRequest request){
        userService.save(request); // 회원 가입 메소드 호출
        return "redirect:/login"; // 회원 가입이 완료된 후 로그인 페이지로 이동
    }
    // 로그아웃 메서드
    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response){
        new SecurityContextLogoutHandler().logout(request, response,
                SecurityContextHolder.getContext().getAuthentication());
        return "redirect:/login";
    }
}