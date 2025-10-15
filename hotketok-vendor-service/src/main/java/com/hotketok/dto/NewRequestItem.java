package com.hotketok.dto;

import com.hotketok.domain.enums.ConstructCategory;

public record NewRequestItem(
        Long requestId, ConstructCategory category, String address, String estimateTime
) {}

