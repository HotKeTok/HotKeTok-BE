package com.hotketok.dto;

import com.hotketok.domain.enums.ConstructCategory;
import com.hotketok.domain.enums.PayType;
import com.hotketok.domain.enums.Status;

import java.time.LocalDateTime;
import java.util.List;

public record RequestFormInfoResponse(
        Status status,
        ConstructCategory category,
        LocalDateTime requestSchedule,
        String currentAddress,
        String currentNumber,
        PayType payType,
        List<String> imagesUrl,
        String description
) {
}
