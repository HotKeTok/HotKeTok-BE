package com.hotketok.dto.internalApi;
import com.hotketok.domain.RequestForm;
import com.hotketok.domain.enums.ConstructCategory;

public record RequestFormListResponse(
        Long requestFormId,
        String address,
        ConstructCategory category
) {
    public static RequestFormListResponse from(RequestForm requestForm) {
        return new RequestFormListResponse(
                requestForm.getId(),
                requestForm.getAddress(),
                requestForm.getCategory()
        );
    }
}