package com.hotketok.dto.internalApi;

import com.hotketok.domain.RequestForm;
import com.hotketok.domain.enums.ConstructCategory;
import com.hotketok.domain.enums.PayType;

public record RequestFormDetailResponse(
        Long requestFormId,
        String address,
        ConstructCategory category,
        PayType payType,
        Long payerId
) {
    public static RequestFormDetailResponse from(RequestForm requestForm) {
        return new RequestFormDetailResponse(
                requestForm.getId(),
                requestForm.getAddress(),
                requestForm.getCategory(),
                requestForm.getPayType(),
                requestForm.getPayerId()
        );
    }
}