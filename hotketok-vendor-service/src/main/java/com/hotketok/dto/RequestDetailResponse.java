package com.hotketok.dto;

import com.hotketok.domain.enums.ConstructCategory;
import com.hotketok.domain.enums.PayType;
import com.hotketok.dto.internalApi.UserInfoDetailResponse;

import java.time.LocalDateTime;
import java.util.List;

public record RequestDetailResponse(
        ConstructCategory category,
        String address,
        LocalDateTime estimateTime,
        PayType payerType,
        String payerName,
        String phoneNumber,
        String comment,
        List<String> requestImage
) {
}