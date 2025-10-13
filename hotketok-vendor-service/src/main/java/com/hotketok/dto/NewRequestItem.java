package com.hotketok.dto;

public record NewRequestItem(
        Long requestId, com.hotketok.domain.enums.ConstructCategory category, String address, String estimateTime
) {}

