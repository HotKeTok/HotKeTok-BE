package com.hotketok.dto.internalApi;

import com.hotketok.domain.RequestForm;
import com.hotketok.domain.RequestFormImage;
import com.hotketok.domain.enums.ConstructCategory;
import com.hotketok.domain.enums.PayType;

import java.util.List;

public record RequestFormDetailResponse(
        Long requestFormId,
        String address,
        ConstructCategory category,
        PayType payType,
        Long payerId,
        List<String> requestImages,
        String requestDescription
) {
    public static RequestFormDetailResponse from(RequestForm requestForm) {
        List<String> imageUrls = requestForm.getImages().stream()
                .map(RequestFormImage::getImageUrl)
                .toList();

        return new RequestFormDetailResponse(
                requestForm.getId(),
                requestForm.getAddress() + " " + requestForm.getNumber(),
                requestForm.getCategory(),
                requestForm.getPayType(),
                requestForm.getPayerId(),
                imageUrls,
                requestForm.getDescription()
        );
    }
}