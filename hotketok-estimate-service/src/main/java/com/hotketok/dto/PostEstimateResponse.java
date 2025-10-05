package com.hotketok.dto;

import com.hotketok.domain.Estimate;
import com.hotketok.domain.enums.Category;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PostEstimateResponse (
        Long estimateId,
        String address,
        Category category,
        LocalDateTime estimateTime,
        BigDecimal estimatePrice,
        String comment
) {
    public static PostEstimateResponse from(Estimate estimate, String address, Category category) {
        return new PostEstimateResponse(
                estimate.getId(),
                address,
                category,
                estimate.getCreatedAt(),
                estimate.getEstimatePrice(),
                estimate.getComment()
        );
    }
}
