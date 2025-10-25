package com.hotketok.dto.internalApi;

public record VendorProfileResponse(
        Long vendorId,
        String vendorName,
        String vendorProfileImage
) {
}
