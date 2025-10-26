package com.hotketok.dto.internalApi;

import com.hotketok.domain.RequestForm;

public record RequestFormPayerResponse(
        Long payerId
) {
    public static RequestFormPayerResponse from(RequestForm requestForm) {
        return new RequestFormPayerResponse(requestForm.getPayerId());
    }
}