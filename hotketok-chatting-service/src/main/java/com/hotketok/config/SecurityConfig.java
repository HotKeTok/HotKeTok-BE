package com.hotketok.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // WebSocket과 STOMP 통신에서는 CSRF 토큰 필요 없음
                .csrf(csrf -> csrf.disable())

                // HTTP 요청별 접근 제어
                .authorizeHttpRequests(auth -> auth
                        // WebSocket 핸드셰이크 허용
                        .requestMatchers("/ws-stomp/**").permitAll()
                        // STOMP 메시지 경로 (브로커 경로)
                        .requestMatchers("/pub/**", "/sub/**").permitAll()
                        // 그 외의 REST API는 인증 필요
                        .anyRequest().authenticated()
                )

                // 세션 기반 로그인 비활성화 (JWT, WebSocket에서는 불필요)
                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(form -> form.disable())
                .sessionManagement(session -> session.disable());

        return http.build();
    }
}