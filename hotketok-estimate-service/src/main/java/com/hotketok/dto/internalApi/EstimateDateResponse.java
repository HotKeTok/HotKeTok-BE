package com.hotketok.dto.internalApi;

import com.hotketok.domain.Estimate;

public record EstimateDateResponse(Long estimateId, Long requestFormId) {
    public static EstimateDateResponse from(Estimate estimate) {
        return new EstimateDateResponse(estimate.getId(), estimate.getRequestFormId());
    }
}