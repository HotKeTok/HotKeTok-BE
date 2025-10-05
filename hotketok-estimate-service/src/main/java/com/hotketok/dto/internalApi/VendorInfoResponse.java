package com.hotketok.dto.internalApi;

public record VendorInfoResponse(
        Long vendorId,
        String name,
        String image,
        Long userId,
        String vendorNumber
) {}