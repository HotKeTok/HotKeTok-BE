package com.hotketok.dto.internalApi;
public record UserProfileResponse(
        Long userId,
        String userName,
        String profileImageUrl
) {
}