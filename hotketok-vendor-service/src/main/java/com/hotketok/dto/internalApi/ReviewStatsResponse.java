package com.hotketok.dto.internalApi;

public record ReviewStatsResponse(
        int reviewCount,
        double averageRate
) {}
