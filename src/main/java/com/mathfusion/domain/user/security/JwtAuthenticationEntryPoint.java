package com.mathfusion.domain.user.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mathfusion.domain.user.exception.JwtErrorCode;
import com.mathfusion.domain.user.exception.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        // 기본 INVALID_TOKEN 반환
        JwtException jwtException = new JwtException(JwtErrorCode.INVALID_TOKEN);

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        // ErrorResponse 대신 바로 code/message JSON 내려줌
        String errorJson = objectMapper.writeValueAsString(
                new ErrorBody(jwtException.getErrorCode().getCode(),
                        jwtException.getErrorCode().getMessage())
        );

        response.getWriter().write(errorJson);
    }

    // 간단한 내부 DTO
    private record ErrorBody(String code, String message) {}
}
