package com.hotketok.dto.internalApi;

import com.hotketok.domain.enums.SenderType;

public record UserRoleInfoResponse(
        Long userId,
        SenderType role
) {
}
