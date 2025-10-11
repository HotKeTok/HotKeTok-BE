package com.hotketok.dto.internalApi;

import com.hotketok.domain.Estimate;
import com.hotketok.domain.enums.Status;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SimpleEstimateResponse(
        Long estimateId,
        Long requestFormId,
        Long vendorId,
        BigDecimal estimatePrice,
        String estimateComment,
        LocalDateTime estimateTime,
        Status status
) {
    public static SimpleEstimateResponse from(Estimate estimate) {
        return new SimpleEstimateResponse(
                estimate.getId(),
                estimate.getRequestFormId(),
                estimate.getVendorId(),
                estimate.getEstimatePrice(),
                estimate.getComment(),
                estimate.getCreatedAt(),
                estimate.getStatus()
        );
    }
}