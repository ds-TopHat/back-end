package com.mathfusion.domain.user.security;

import com.mathfusion.domain.user.exception.JwtErrorCode;
import com.mathfusion.domain.user.exception.JwtException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
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

    // Access Token 생성
    public String generateToken(String email) {
        return createToken(email, accessExpirationMs);
    }

    // Refresh Token 생성
    public String generateRefreshToken(String email) {
        return createToken(email, refreshExpirationMs);
    }

    // 공통 토큰 생성
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
            // JWT 생성 실패 → 일반 JwtException
            throw new JwtException(JwtErrorCode.INVALID_TOKEN);
        }
    }

    // 토큰 검증 (Access/Refresh 구분 없이 사용 가능)
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            throw new JwtException(JwtErrorCode.EXPIRED_TOKEN);
        } catch (io.jsonwebtoken.JwtException | IllegalArgumentException e) {
            throw new JwtException(JwtErrorCode.INVALID_TOKEN);
        }
    }

    // 토큰에서 이메일 추출
    public String getEmailFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject();
        } catch (ExpiredJwtException e) {
            throw new JwtException(JwtErrorCode.EXPIRED_TOKEN);
        } catch (io.jsonwebtoken.JwtException | IllegalArgumentException e) {
            throw new JwtException(JwtErrorCode.INVALID_TOKEN);
        }
    }
}
