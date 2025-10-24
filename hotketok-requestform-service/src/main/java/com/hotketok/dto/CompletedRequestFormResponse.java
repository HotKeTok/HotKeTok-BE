package com.hotketok.dto;

import com.hotketok.domain.enums.ConstructCategory;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CompletedRequestFormResponse(
        Long requestFormId,
        ConstructCategory category,
        LocalDateTime requestSchedule,
        BigDecimal estimatePrice,
        String number
) {
}
