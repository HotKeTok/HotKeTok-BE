package com.hotketok.dto;

public record JwtToken (
        String accessToken,
        String refreshToken
){}
