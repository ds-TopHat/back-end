package com.mathfusion.domain.user.controller;

import com.mathfusion.domain.user.dto.UserRequest;
import com.mathfusion.domain.user.dto.UserResponse;
import com.mathfusion.domain.user.exception.JwtException;
import com.mathfusion.domain.user.exception.RefreshTokenException;
import com.mathfusion.domain.user.exception.UserException;
import com.mathfusion.domain.user.security.JwtUtil;
import com.mathfusion.domain.user.service.AuthService;
import com.mathfusion.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v0/users")
@Validated
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthService authService;
    private final JwtUtil jwtUtil;

    // ===================== 회원가입 =====================
    @Operation(summary = "회원가입")
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody @Validated UserRequest.SignupRequest request) {
        try {
            Long userId = userService.signup(request);

            UserResponse.SignupResponse response = UserResponse.SignupResponse.builder()
                    .id(userId)
                    .email(request.getEmail())
                    .build();

            return ResponseEntity.ok(response);
        } catch (UserException e) {
            return ResponseEntity.status(400).body("회원가입 실패: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("회원가입 중 오류 발생: " + e.getMessage());
        }
    }

    // ===================== 로그인 =====================
    @Operation(summary = "로그인")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Validated UserRequest.LoginRequest request) {
        try {
            UserResponse.LoginResponse response = authService.login(request.getEmail(), request.getPassword());
            return ResponseEntity.ok(response);
        } catch (UserException e) {
            return ResponseEntity.status(400).body("로그인 실패: " + e.getMessage());
        } catch (JwtException e) {
            return ResponseEntity.status(401).body("로그인 실패: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("로그인 중 오류 발생: " + e.getMessage());
        }
    }

    // ===================== 회원탈퇴 =====================
    @Operation(summary = "회원탈퇴")
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUser(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.badRequest().body("JWT 토큰이 없습니다.");
            }

            String token = authHeader.substring(7);

            // JWT 검증
            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(401).body("유효하지 않은 토큰입니다.");
            }

            String identifier = jwtUtil.getEmailFromToken(token);

            userService.deleteByIdentifier(identifier);

            return ResponseEntity.ok("회원탈퇴 완료");
        } catch (UserException e) {
            return ResponseEntity.status(400).body("회원탈퇴 실패: " + e.getMessage());
        } catch (JwtException e) {
            return ResponseEntity.status(401).body("JWT 오류: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("회원탈퇴 중 오류 발생: " + e.getMessage());
        }
    }

    // ===================== 리프레시 토큰 =====================
    @Operation(summary = "리프레시 토큰")

    @PostMapping("/refreshtoken")
    public ResponseEntity<?> reissue(@RequestBody UserRequest.RefreshTokenRequest request) {
        try {
            UserResponse.LoginResponse newTokenResponse = authService.reissue(request.getRefreshToken());
            return ResponseEntity.ok(newTokenResponse);

        } catch (JwtException e) {
            // JSON 형태 통일
            Map<String, String> body = Map.of(
                    "code", "401",
                    "message", "리프레시 토큰 실패: " + e.getMessage()
            );
            return ResponseEntity.status(401).body(body);

        } catch (RefreshTokenException e) {
            Map<String, String> body = Map.of(
                    "code", "401",
                    "message", "Refresh Token 문제: " + e.getMessage()
            );
            return ResponseEntity.status(401).body(body);

        } catch (Exception e) {
            Map<String, String> body = Map.of(
                    "code", "SERVER001",
                    "message", "서버 오류: " + e.getMessage()
            );
            return ResponseEntity.status(500).body(body);
        }
    }


}



