package com.hotketok.dto.internalApi;

import com.hotketok.domain.enums.Status;

public record RequestFormAddressStatusResponse(
        Long requestFormId,
        String address,
        Status status
) {
}
