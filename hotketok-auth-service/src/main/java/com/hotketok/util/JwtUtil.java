package com.hotketok.util;

import com.hotketok.domain.Role;
import com.hotketok.dto.JwtToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-exp-ms}")
    private long accessExpMs; // 1h

    @Value("${jwt.refresh-exp-ms}")
    private long refreshExpMs; // 14d

    public JwtToken issue(Long userId, Role role){
        log.info("[JwtUtil] issue tokens for user=" + userId);
        String access = Jwts.builder()
                .setSubject("access")
                .claim("userId", userId)
                .claim("role", role.getValue())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+accessExpMs))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
        String refresh = Jwts.builder()
                .setSubject("refresh")
                .claim("userId", userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+refreshExpMs))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
        return new JwtToken(access, refresh);
    }

    public Claims parse(String token){
        log.info("[JwtUtil] parse token");
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    public long getRemainingTime(String token) {
        Date exp = parse(token).getExpiration();
        return exp.getTime() - System.currentTimeMillis();
    }

    public long getRefreshExpMs() { return refreshExpMs; }

}
