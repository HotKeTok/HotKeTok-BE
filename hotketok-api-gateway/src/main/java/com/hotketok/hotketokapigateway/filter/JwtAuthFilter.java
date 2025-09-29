package com.hotketok.hotketokapigateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtAuthFilter extends AbstractGatewayFilterFactory<JwtAuthFilter.Config> {

    private final JwtParser jwtParser;
    @Value("${jwt.secret}")
    private String secret;

    public JwtAuthFilter(){ super(Config.class); this.jwtParser = Jwts.parser(); }

    @Override
    public GatewayFilter apply(Config config){
        return (exchange, chain) -> {
            String token = extract(exchange.getRequest());
            if (token == null) {
                log.info("Token이 비어있습니다.");
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
            try {
                Claims claims = Jwts.parser()
                        .setSigningKey(secret)
                        .parseClaimsJws(token)
                        .getBody();

                String userId = claims.get("userId").toString();
                String role = claims.get("role").toString();

                ServerHttpRequest mutated = exchange.getRequest().mutate()
                        .header("userId", userId)
                        .header("role", role)
                        .build();

                return chain.filter(exchange.mutate().request(mutated).build());
            } catch (Exception e) {
                log.error("JWT 검증 실패: " + e.getClass().getSimpleName() + " - " + e.getMessage());
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        };
    }

    private String extract(ServerHttpRequest req){
        String h = req.getHeaders().getFirst("Authorization");
        return (h!=null && h.startsWith("Bearer "))? h.substring(7): null;
    }
    public static class Config {}
}

