package com.hotketok.dto;

import com.hotketok.domain.enums.Category;
import com.hotketok.domain.enums.PayType;

import java.time.LocalDateTime;
import java.util.List;

public record RequestFormInfoResponse(
        Category category,
        LocalDateTime requestSchedule,
        String currentAddress,
        String currentNumber,
        PayType payType,
        List<String> imagesUrl,
        String description
) {
}
