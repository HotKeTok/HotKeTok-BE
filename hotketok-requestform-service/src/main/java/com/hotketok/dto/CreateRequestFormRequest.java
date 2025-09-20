package com.hotketok.dto;

import com.hotketok.domain.enums.Category;
import com.hotketok.domain.enums.PayType;

import java.time.LocalDateTime;

public record CreateRequestFormRequest(
        PayType payType,
        Category category,
        String description,
        LocalDateTime requestSchedule
) {
}
