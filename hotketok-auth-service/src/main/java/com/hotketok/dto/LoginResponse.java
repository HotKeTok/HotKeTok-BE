package com.hotketok.dto;

import com.hotketok.domain.Role;

public record LoginResponse(
        JwtToken jwtToken,
        Role role,
        boolean onBoardingStageFlag
) {
}
