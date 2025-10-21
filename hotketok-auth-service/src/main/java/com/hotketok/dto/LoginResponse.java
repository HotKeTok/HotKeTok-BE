package com.hotketok.dto;

import com.hotketok.domain.Role;

public record LoginResponse(
        Long userId,
        JwtToken jwtToken,
        Role role,
        boolean onBoardingStageFlag
) {
}
