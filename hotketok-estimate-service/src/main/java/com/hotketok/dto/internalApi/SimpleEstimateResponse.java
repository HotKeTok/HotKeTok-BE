package com.hotketok.dto.internalApi;

import com.hotketok.domain.Estimate;
import com.hotketok.domain.enums.Status;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

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
    public static SimpleEstimateResponse from(Estimate estimate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd / a hh:mm", Locale.KOREAN);
        String formattedTime = estimate.getCreatedAt().format(formatter);

        return new SimpleEstimateResponse(
                estimate.getId(),
                estimate.getRequestFormId(),
                estimate.getVendorId(),
                estimate.getEstimatePrice(),
                estimate.getComment(),
                formattedTime,
                estimate.getStatus(),
                estimate.getDecisionLater()
        );
    }
}