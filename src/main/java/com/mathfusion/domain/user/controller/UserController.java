package com.mathfusion.domain.user.controller;

import com.mathfusion.domain.user.dto.UserRequest;
import com.mathfusion.domain.user.dto.UserResponse;
import com.mathfusion.domain.user.security.JwtUtil;
import com.mathfusion.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v0/users")
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    private final JwtUtil jwtUtil;

    // 회원가입
    @Operation(summary = "회원가입")
    @PostMapping("/signup")
    public ResponseEntity<UserResponse.SignupResponse> signup(
            @RequestBody @Validated UserRequest.SignupRequest request) {

        Long userId = userService.signup(request);

        UserResponse.SignupResponse response = UserResponse.SignupResponse.builder()
                .id(userId)
                .email(request.getEmail())
                .build();

        return ResponseEntity.ok(response);
    }

    // 로그인
    @Operation(summary = "로그인")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Validated UserRequest.LoginRequest request) {

        try {
            // AuthenticationManager로 인증
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // JWT 토큰 생성
            String token = jwtUtil.generateToken(request.getEmail());

            // 응답 반환
            UserResponse.LoginResponse response = UserResponse.LoginResponse.builder()
                    .email(request.getEmail())
                    .message("로그인 성공")
                    .token(token)
                    .build();

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace(); // 어떤 예외인지 콘솔 확인
            return ResponseEntity.status(500).body("로그인 실패: " + e.getMessage());
        }
    }


    //회원 탈퇴
    @Operation(summary = "회원탈퇴")
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUser(@RequestHeader("Authorization") String authHeader){

        //토큰에서 실제 jwt 추출
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("jwt 토큰이 없습니다.");
        }
        String token = authHeader.substring(7);

        // JWT 검증 (try-catch 추가)
        try {
            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(401).body("유효하지 않은 토큰입니다.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("서버 내부 오류: " + e.getMessage());
        }

        // 토큰에서 이메일 추출
        String email = jwtUtil.getEmailFromToken(token);

        // DB에서 회원 삭제
        userService.deleteByEmail(email);

        // 탈퇴 완료 메시지 반환
        return ResponseEntity.ok("회원탈퇴 완료");

    }

}
