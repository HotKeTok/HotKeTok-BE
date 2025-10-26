package com.hotketok.dto.internalApi;

import com.hotketok.domain.enums.Status;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EstimateInfoResponse(
        Long estimateId,
        Long requestFormId,
        String estimateTime,
        Status status,
        BigDecimal estimatePrice,
        String estimateComment,
        Boolean decisionLater
) {}