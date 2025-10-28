package com.hotketok.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .addFilterBefore(corsFilter(), SecurityContextHolderFilter.class)
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/ws-stomp/**").permitAll() // ← handshake 허용
                        .requestMatchers("/sub/**", "/pub/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/api/chatting-service/users/rooms").permitAll()
                        .requestMatchers("/api/chatting-service/rooms").permitAll()
                        .requestMatchers("/api/chatting-service/rooms/messages").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(form -> form.disable());

        http.headers(headers ->
                headers.frameOptions(frameOptions -> frameOptions.sameOrigin())
        );

        return http.build();
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        // 패턴을 사용하되, Spring Security 필터에 의해 모든 요청에 대해 강제 적용됩니다.
        config.setAllowedOriginPatterns(List.of("http://localhost:*", "https://*.shop", "*"));
        config.setAllowedMethods(List.of("*"));
        config.setAllowedHeaders(List.of("*"));
        source.registerCorsConfiguration("/**", config); // 모든 경로에 CORS 설정 적용
        return new CorsFilter(source);
    }
}