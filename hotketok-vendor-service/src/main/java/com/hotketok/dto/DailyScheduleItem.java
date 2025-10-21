package com.hotketok.dto;

import com.hotketok.domain.enums.PayType;
import com.hotketok.domain.enums.Status;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DailyScheduleItem(
        Long estimateId, com.hotketok.domain.enums.ConstructCategory category, String address, String estimateTime,
        BigDecimal estimatePrice, PayType payType, String payerName, String phoneNumber,
        String estimateComment, Status status
) {}
