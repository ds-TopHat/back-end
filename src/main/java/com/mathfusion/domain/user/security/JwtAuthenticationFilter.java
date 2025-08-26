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
            "/default-ui.css"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            String token = tokenProvider.extractToken(request.getHeader(HttpHeaders.AUTHORIZATION));

            if (token == null || !tokenProvider.validateToken(token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":\"JWT001\",\"message\":\"유효하지 않은 토큰입니다.\"}");
                return;
            }

            Authentication authentication = tokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":\"JWT001\",\"message\":\"유효하지 않은 토큰입니다.\"}");
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
