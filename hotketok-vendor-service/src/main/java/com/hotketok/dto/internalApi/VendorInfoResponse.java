package com.hotketok.dto.internalApi;

import com.hotketok.domain.Vendor;

public record VendorInfoResponse(
        Long vendorId,
        String name,
        String image,
        Long userId
) {
    public static VendorInfoResponse from(Vendor vendor) {
        return new VendorInfoResponse(
                vendor.getId(),
                vendor.getName(),
                vendor.getImage(),
                vendor.getUserId()
        );
    }
}