package com.hotketok.dto.internalApi;

import com.hotketok.domain.RequestForm;
import com.hotketok.domain.enums.ConstructCategory;
import java.time.LocalDateTime;

public record RequestFormSimpleResponse(
        Long requestId,
        ConstructCategory category,
        String address,
        LocalDateTime estimateTime
) {
    public static RequestFormSimpleResponse from(RequestForm requestForm) {
        return new RequestFormSimpleResponse(
                requestForm.getId(),
                requestForm.getCategory(),
                requestForm.getAddress() + " " + requestForm.getNumber(),
                requestForm.getRequestSchedule()
        );
    }
}