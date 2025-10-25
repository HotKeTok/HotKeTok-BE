package com.hotketok.dto.internalApi;

import com.hotketok.domain.enums.ConstructCategory;
import com.hotketok.domain.enums.PayType;

import java.time.LocalDateTime;
import java.util.List;

public record RequestFormDetailResponse(
        Long requestFormId,
        String address,
        ConstructCategory category,
        PayType payType,
        Long payerId,
        List<String> requestImages,
        String requestDescription,
        LocalDateTime estimateTime
) {
}