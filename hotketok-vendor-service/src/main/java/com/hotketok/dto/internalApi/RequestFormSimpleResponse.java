package com.hotketok.dto.internalApi;

import com.hotketok.domain.enums.ConstructCategory;

import java.time.LocalDateTime;

public record RequestFormSimpleResponse(
        Long requestId,
        ConstructCategory category,
        String address,
        LocalDateTime estimateTime
) {
}