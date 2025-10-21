package com.hotketok.dto;

import com.hotketok.domain.enums.ConstructCategory;
import com.hotketok.domain.enums.PayType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MatchingEstimateInfoResponse(
        Long estimateId, ConstructCategory category, String address, String estimateTime,
        BigDecimal estimatePrice, PayType payType, String payerName,
        String phoneNumber,
        String estimateComment
) {}