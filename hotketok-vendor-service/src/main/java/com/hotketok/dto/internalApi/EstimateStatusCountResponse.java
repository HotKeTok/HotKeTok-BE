package com.hotketok.dto.internalApi;

public record EstimateStatusCountResponse(
        long processingRequest,
        long doneRequest
) {}