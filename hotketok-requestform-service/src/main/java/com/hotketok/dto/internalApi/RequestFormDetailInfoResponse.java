package com.hotketok.dto.internalApi;

import com.hotketok.domain.RequestForm;
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
    public static RequestFormDetailInfoResponse from(RequestForm requestForm, List<String> images) {
        return new RequestFormDetailInfoResponse(
                requestForm.getId(),
                requestForm.getAuthorId(),
                requestForm.getPayerId(),
                requestForm.getPayType(),
                requestForm.getCategory(),
                requestForm.getAddress(),
                requestForm.getRequestSchedule(),
                requestForm.getDescription(),
                images // 이미지를 직접 전달받아 사용
        );
    }
}