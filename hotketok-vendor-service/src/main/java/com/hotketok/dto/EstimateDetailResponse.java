package com.hotketok.dto;

import com.hotketok.domain.enums.ConstructCategory;
import com.hotketok.domain.enums.PayType;
import com.hotketok.domain.enums.Status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record EstimateDetailResponse(
        Long estimateId,
        ConstructCategory category,
        String address,
        LocalDateTime estimateTime,
        BigDecimal estimatePrice,
        PayType payType,
        String payerName,
        String phoneNumber,
        List<String> requestImage,
        String requestDescription,
        String estimateComment,
        Status status,
        Boolean decisionLater,
        Long userId,
        Long vendorId
) {}