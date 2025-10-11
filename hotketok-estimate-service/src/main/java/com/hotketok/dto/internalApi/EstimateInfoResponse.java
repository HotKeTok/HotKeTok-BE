package com.hotketok.dto.internalApi;
import com.hotketok.domain.Estimate;
import com.hotketok.domain.enums.Status;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EstimateInfoResponse(
        Long estimateId,
        Long requestFormId,
        LocalDateTime estimateTime,
        Status status,
        BigDecimal estimatePrice,
        String estimateComment
) {
    public static EstimateInfoResponse from(Estimate estimate) {
        return new EstimateInfoResponse(
                estimate.getId(),
                estimate.getRequestFormId(),
                estimate.getCreatedAt(),
                estimate.getStatus(),
                estimate.getEstimatePrice(),
                estimate.getComment()
        );
    }
}