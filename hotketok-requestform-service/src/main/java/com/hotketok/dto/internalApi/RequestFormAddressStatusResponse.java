package com.hotketok.dto.internalApi;

import com.hotketok.domain.RequestForm;
import com.hotketok.domain.enums.Status;

public record RequestFormAddressStatusResponse(
        String address,
        Status status
) {
    public static RequestFormAddressStatusResponse from(RequestForm requestForm) {
        return new RequestFormAddressStatusResponse(
                requestForm.getAddress() + " " + requestForm.getNumber(),
                requestForm.getStatus()
        );
    }
}
