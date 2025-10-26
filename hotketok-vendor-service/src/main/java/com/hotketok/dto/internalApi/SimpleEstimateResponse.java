package com.hotketok.dto.internalApi;

import com.hotketok.domain.enums.Status;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SimpleEstimateResponse(
        Long estimateId,
        Long requestFormId,
        Long vendorId,
        BigDecimal estimatePrice,
        String estimateComment,
        String estimateTime,
        Status status,
        Boolean decisionLater
) {
}