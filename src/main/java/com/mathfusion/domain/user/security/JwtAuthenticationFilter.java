package com.mathfusion.domain.user.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;

    // permitAll 경로 정의
    private static final Set<String> PERMIT_PATHS = Set.of(
            "/api/v0/users/signup",
            "/api/v0/users/login",
            "/favicon.ico",
            "/error",
            "/health"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1. 요청 헤더에서 토큰 추출
        String token = tokenProvider.extractToken(request.getHeader(HttpHeaders.AUTHORIZATION));

        // 2. 토큰 검증 후 Authentication 등록
        if (token != null && tokenProvider.validateToken(token)) {
            Authentication authentication = tokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 3. 다음 필터로 이동
        filterChain.doFilter(request, response);
    }

    /**
     * permitAll 경로는 JWT 검증 필터를 아예 적용하지 않음
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        String method = request.getMethod();

        // OPTIONS 요청은 항상 필터 건너뛰기
        if ("OPTIONS".equalsIgnoreCase(method)) {
            return true;
        }

        // permitAll 경로 확인
        for (String permitPath : PERMIT_PATHS) {
            if (path.equals(permitPath) || path.startsWith(permitPath + "/")) {
                return true;
            }
        }

        return false;
    }
}
