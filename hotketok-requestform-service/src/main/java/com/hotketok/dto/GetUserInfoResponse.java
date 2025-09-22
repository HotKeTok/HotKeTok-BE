package com.hotketok.dto;

public record GetUserInfoResponse(
        Long userId,
        Long proprietorId,
        String address
) {
}
