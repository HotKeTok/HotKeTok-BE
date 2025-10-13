package com.hotketok.dto.internalApi;
import com.hotketok.domain.Estimate;
import com.hotketok.domain.enums.Status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public record EstimateInfoResponse(
        Long estimateId,
        Long requestFormId,
        String estimateTime,
        Status status,
        BigDecimal estimatePrice,
        String estimateComment
) {
    public static EstimateInfoResponse from(Estimate estimate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd / a hh:mm", Locale.KOREAN);
        String formattedTime = estimate.getCreatedAt().format(formatter);

        return new EstimateInfoResponse(
                estimate.getId(),
                estimate.getRequestFormId(),
                formattedTime,
                estimate.getStatus(),
                estimate.getEstimatePrice(),
                estimate.getComment()
        );
    }
}