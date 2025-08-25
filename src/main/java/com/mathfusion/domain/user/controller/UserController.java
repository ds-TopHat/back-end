package com.mathfusion.domain.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mathfusion.domain.user.dto.UserRequest;
import com.mathfusion.domain.user.dto.UserResponse;
import com.mathfusion.domain.user.security.JwtUtil;
import com.mathfusion.domain.user.service.AuthService;
import com.mathfusion.domain.user.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v0/users")
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    private final JwtUtil jwtUtil;

    // 회원가입
    @Operation(summary = "회원가입")
    @PostMapping("/signup")
    public ResponseEntity<?> signup(
            @RequestBody @Validated UserRequest.SignupRequest request) {

        try {
            Long userId = userService.signup(request);

            UserResponse.SignupResponse response = UserResponse.SignupResponse.builder()
                    .id(userId)
                    .email(request.getEmail())
                    .build();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("회원가입 실패: " + e.getMessage());
        }
    }

    // 로그인
    @Operation(summary = "로그인")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Validated UserRequest.LoginRequest request) {

        try {
            UserResponse.LoginResponse response = authService.login(request.getEmail(), request.getPassword());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("로그인 실패: " + e.getMessage());
        }
    }

    //회원 탈퇴
    @Operation(summary = "회원탈퇴")
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUser(@RequestHeader("Authorization") String authHeader){

        try {
            //토큰에서 실제 jwt 추출
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.badRequest().body("jwt 토큰이 없습니다.");
            }
            String token = authHeader.substring(7);

            // JWT 검증
            try {
                if (!jwtUtil.validateToken(token)) {
                    return ResponseEntity.status(401).body("유효하지 않은 토큰입니다.");
                }
            } catch (Exception e) {
                return ResponseEntity.status(500).body("JWT 검증 오류: " + e.getMessage());
            }

            // 토큰에서 이메일 추출
            String email;
            try {
                email = jwtUtil.getEmailFromToken(token);
            } catch (Exception e) {
                return ResponseEntity.status(500).body("토큰에서 이메일 추출 오류: " + e.getMessage());
            }

            // DB에서 회원 삭제
            try {
                userService.deleteByEmail(email);
            } catch (Exception e) {
                return ResponseEntity.status(500).body("회원 삭제 오류: " + e.getMessage());
            }

            // 탈퇴 완료 메시지 반환
            return ResponseEntity.ok("회원탈퇴 완료");

        } catch (Exception e) {
            return ResponseEntity.status(500).body("회원탈퇴 중 예상치 못한 오류: " + e.getMessage());
        }
    }

    //refresh token
    @Operation(summary = "리프레시 토큰")
    @PostMapping("/refreshtoken")
    public ResponseEntity<?> reissue(@RequestBody String refreshToken) {
        try {
            String newAccessToken = authService.reissue(refreshToken);
            return ResponseEntity.ok(newAccessToken);
        } catch (Exception e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }
}