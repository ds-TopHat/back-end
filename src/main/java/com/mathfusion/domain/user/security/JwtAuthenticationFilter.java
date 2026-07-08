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
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;
    private static final AntPathMatcher pathMatcher = new AntPathMatcher();

    // permitAll 경로 정의
    private static final Set<String> PERMIT_PATHS = Set.of(
            "/api/v0/users/signup",
            "/api/v0/users/login",
            "/api/v0/email-auth/request-code",
            "/api/v0/email-auth/verify-code",
            "/api/v0/users/refreshtoken",
            "/favicon.ico",
            "/error",
            "/health",
            "/css/**",
            "/images/**",
            "/js/**",
            "/lib/**",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-ui.html",
            "/api/v0/email-auth/**",
            "/default-ui.css",
            "/api/auth/kakao/**"
    );

    //cors에러 배포 서버 허용
    private void setCorsHeaders(HttpServletRequest request, HttpServletResponse response) {
        String origin = request.getHeader("Origin");

        List<String> allowedOrigins = List.of(
                "http://localhost:5173",
                "https://topmapi.duckdns.org",
                "https://tophatmapi.duckdns.org",
                "https://ds-tophat.vercel.app"
        );

        if (origin != null && allowedOrigins.contains(origin)) {
            response.setHeader("Access-Control-Allow-Origin", origin);
            response.setHeader("Vary", "Origin"); // 캐시 문제 방지
        }

        response.setHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "*");
        response.setHeader("Access-Control-Allow-Credentials", "true");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            String token = tokenProvider.extractToken(authHeader);

            // 토큰이 아예 없는 경우
            if (token == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                setCorsHeaders(request, response); //cors에러 처리
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":\"401\",\"message\":\"JWT 토큰이 없습니다.\"}");
                return;
            }

            // 토큰이 유효하지 않은 경우
            if (!tokenProvider.validateToken(token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                setCorsHeaders(request, response); //cors에러 처리
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":\"401\",\"message\":\"유효하지 않은 토큰입니다.\"}");
                return;
            }

            Authentication authentication = tokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            setCorsHeaders(request, response);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":\"401\",\"message\":\"유효하지 않은 토큰입니다.\"}");
        }
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
            if (pathMatcher.match(permitPath, path)) {
                return true;
            }
        }

        return false;
    }
}
