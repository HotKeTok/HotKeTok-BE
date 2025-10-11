package com.hotketok.dto;

import com.hotketok.domain.enums.ConstructCategory;
import com.hotketok.domain.enums.PayType;

import java.time.LocalDateTime;

public record CreateRequestFormRequest(
        PayType payType,
        ConstructCategory category,
        String description,
        LocalDateTime requestSchedule,
        String address,
        String number
) {
}
