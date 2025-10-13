package com.hotketok.dto.internalApi;

public record EstimateStatusCountResponse(
        long newRequest,
        long processingRequest,
        long doneRequest
) {}