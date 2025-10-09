package com.hotketok.dto.internalApi;

import com.hotketok.domain.enums.ConstructCategory;
import com.hotketok.domain.enums.PayType;

public record RequestFormDetailResponse(
        Long requestFormId,
        String address,
        ConstructCategory category,
        PayType payType,
        Long payerId
) {
}