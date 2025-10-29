package com.hotketok.dto.internalApi;

import com.hotketok.domain.enums.ConstructCategory;
import com.hotketok.domain.enums.PayType;

import java.time.LocalDateTime;
import java.util.List;

public record RequestFormDetailInfoResponse(
        Long requestFormId,
        Long authorId,
        Long payerId,
        PayType payType,
        ConstructCategory category,
        String address,
        LocalDateTime estimateTime,
        String requestDescription,
        List<String> requestImages
) {
}