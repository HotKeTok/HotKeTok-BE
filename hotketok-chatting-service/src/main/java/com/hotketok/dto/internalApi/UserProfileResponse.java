package com.hotketok.dto.internalApi;

import com.hotketok.domain.enums.SenderType;

public record UserProfileResponse(
        Long userId,
        String userName,
        String profileImageUrl,
        SenderType role
) {}
