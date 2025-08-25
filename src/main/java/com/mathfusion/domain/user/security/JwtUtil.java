package com.mathfusion.domain.user.security;

import com.mathfusion.domain.user.exception.JwtErrorCode;
import com.mathfusion.domain.user.exception.JwtException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
@Slf4j
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms}")
    private long accessExpirationMs;

    @Value("${jwt.refresh-expiration-ms}")
    private long refreshExpirationMs;

    private Key getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String email) {
        return createToken(email, accessExpirationMs);
    }

    public String generateRefreshToken(String email) {
        return createToken(email, refreshExpirationMs);
    }

    private String createToken(String email, long expirationTime) {
        try {
            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + expirationTime);

            return Jwts.builder()
                    .setSubject(email)
                    .setIssuedAt(now)
                    .setExpiration(expiryDate)
                    .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                    .compact();
        } catch (Exception e) {
            log.error("[JwtUtil] 토큰 생성 실패: {}", e.getMessage(), e);
            throw new JwtException(JwtErrorCode.INVALID_TOKEN);
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.error("[JwtUtil] 토큰 만료됨: {}", e.getMessage());
            throw new JwtException(JwtErrorCode.EXPIRED_TOKEN);
        } catch (UnsupportedJwtException e) {
            log.error("[JwtUtil] 지원되지 않는 토큰: {}", e.getMessage());
            throw new JwtException(JwtErrorCode.INVALID_TOKEN);
        } catch (MalformedJwtException e) {
            log.error("[JwtUtil] 토큰 구조 오류: {}", e.getMessage());
            throw new JwtException(JwtErrorCode.INVALID_TOKEN);
        } catch (SignatureException e) {
            log.error("[JwtUtil] 서명 검증 실패: {}", e.getMessage());
            throw new JwtException(JwtErrorCode.INVALID_TOKEN);
        } catch (IllegalArgumentException e) {
            log.error("[JwtUtil] 잘못된 토큰 입력: {}", e.getMessage());
            throw new JwtException(JwtErrorCode.INVALID_TOKEN);
        }
    }

    public String getEmailFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject();
        } catch (ExpiredJwtException e) {
            log.error("[JwtUtil] 토큰 만료됨: {}", e.getMessage());
            throw new JwtException(JwtErrorCode.EXPIRED_TOKEN);
        } catch (JwtException | IllegalArgumentException e) {
            log.error("[JwtUtil] 토큰에서 이메일 추출 실패: {}", e.getMessage());
            throw new JwtException(JwtErrorCode.INVALID_TOKEN);
        }
    }

    // 토큰 만료 시간(ms) 가져오기
    public long getExpiration(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getExpiration().getTime();
        } catch (ExpiredJwtException e) {
            return e.getClaims().getExpiration().getTime();
        } catch (io.jsonwebtoken.JwtException | IllegalArgumentException e) {
            throw new JwtException(JwtErrorCode.INVALID_TOKEN);
        }
    }

}
