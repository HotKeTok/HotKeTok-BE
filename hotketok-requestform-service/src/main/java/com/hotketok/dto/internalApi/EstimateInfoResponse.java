package com.hotketok.dto.internalApi;

import com.hotketok.domain.enums.Status;

import java.time.LocalDateTime;

public record EstimateInfoResponse(
        Long estimateId,
        Long requestFormId,
        String estimateTime,
        Status status
) {
}