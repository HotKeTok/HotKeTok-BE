package com.hotketok.dto.internalApi;

public record UserInfoDetailResponse(
        Long userId,
        String name,
        String phoneNumber
) {}