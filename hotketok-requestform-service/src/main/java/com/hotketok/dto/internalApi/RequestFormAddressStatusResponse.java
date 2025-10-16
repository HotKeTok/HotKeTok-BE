package com.hotketok.dto.internalApi;

import com.hotketok.domain.RequestForm;
import com.hotketok.domain.enums.Status;

public record RequestFormAddressStatusResponse(
        Long requestFormId,
        String address,
        Status status
) {
    public static RequestFormAddressStatusResponse from(RequestForm requestForm) {
        return new RequestFormAddressStatusResponse(
                requestForm.getId(),
                requestForm.getAddress() + " " + requestForm.getNumber(),
                requestForm.getStatus()
        );
    }
}
