package com.hotketok.dto;

import com.hotketok.domain.Estimate;
import com.hotketok.domain.enums.Category;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public record PostEstimateResponse (
        Long estimateId,
        String address,
        Category category,
        String estimateTime,
        BigDecimal estimatePrice,
        String comment
) {
    public static PostEstimateResponse from(Estimate estimate, String address, Category category) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd / a hh:mm", Locale.KOREAN);
        String formattedTime = estimate.getCreatedAt().format(formatter);

        return new PostEstimateResponse(
                estimate.getId(),
                address,
                category,
                formattedTime,
                estimate.getEstimatePrice(),
                estimate.getComment()
        );
    }
}
