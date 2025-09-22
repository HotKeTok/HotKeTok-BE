package com.hotketok.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

@Configuration
public class OpenAiFeignConfig {

    @Value("${openai.api.key}")
    private String openAIKey;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            template.header(HttpHeaders.AUTHORIZATION, "Bearer " + openAIKey);
        };
    }
}
