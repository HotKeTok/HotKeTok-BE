package com.hotketok.dto;

import com.hotketok.domain.enums.ConstructCategory;
import com.hotketok.domain.enums.PayType;

import java.time.LocalDateTime;
import java.util.List;

public record RequestFormInfoResponse(
        ConstructCategory category,
        LocalDateTime requestSchedule,
        String currentAddress,
        String currentNumber,
        PayType payType,
        List<String> imagesUrl,
        String description
) {
}
