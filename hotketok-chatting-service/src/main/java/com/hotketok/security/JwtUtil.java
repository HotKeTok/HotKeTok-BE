package com.hotketok.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    public static final String TOKEN_PREFIX = "Bearer ";

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    private String resolveToken(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(TOKEN_PREFIX.length());
        }
        log.warn("Invalid token format: Missing 'Bearer ' prefix or token is null.");
        return null;
    }

    public boolean validateToken(String token) {
        // Bearer 제거
        String cleanToken = resolveToken(token);
        if (cleanToken == null) {
            return false;
        }

        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(cleanToken);
            return true;
        } catch (Exception e) {
            log.error("Invalid JWT: {}", e.getMessage());
            return false;
        }
    }

    public Authentication getAuthentication(String token) {

        // 1. "Bearer " 접두사 제거
        String cleanToken = resolveToken(token);
        if (cleanToken == null) {
            // StompHandler의 validateToken에서 이미 검증했지만, 방어 코드로 추가
            throw new RuntimeException("Invalid token format: Missing 'Bearer ' prefix");
        }

        // 2. 순수 토큰(cleanToken)을 사용하여 Claims 파싱
        Claims claims = Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(cleanToken).getBody();

        Long userId = claims.get("userId", Long.class);
        if (userId == null) {
            throw new RuntimeException("Token does not contain userId claim");
        }
        String userIdStr = String.valueOf(userId); // Principal 이름은 String

        String role = claims.get("role", String.class);
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (role != null) {
            // auth-service에서 Role enum의 getValue()를 넣었으므로 Spring Security 표준인 "ROLE_" 접두사 추가
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
        } else {
            authorities.add(new SimpleGrantedAuthority("NONE"));
        }

        return new UsernamePasswordAuthenticationToken(userIdStr, token, authorities); // 사용자 ID 문자열을 Principal로 사용
    }
}