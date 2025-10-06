package com.hotketok.dto.internalApi;
import com.hotketok.domain.RequestForm;
import com.hotketok.domain.enums.Category;

public record RequestFormListResponse(
        Long requestFormId,
        String address,
        Category category
) {
    public static RequestFormListResponse from(RequestForm requestForm) {
        return new RequestFormListResponse(
                requestForm.getId(),
                requestForm.getAddress(),
                requestForm.getCategory()
        );
    }
}