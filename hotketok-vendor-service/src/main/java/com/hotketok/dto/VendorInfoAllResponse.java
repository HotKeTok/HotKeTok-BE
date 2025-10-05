package com.hotketok.dto;

import com.hotketok.domain.Vendor;
import com.hotketok.domain.enums.Category;
public record VendorInfoAllResponse(
        Long vendorId,
        String name,
        Category category,
        String address,
        String detailAddress,
        String introduction,
        String image,
        int rate
) {
    public static VendorInfoAllResponse from(Vendor vendor) {

        return new VendorInfoAllResponse(
                vendor.getId(),
                vendor.getName(),
                vendor.getCategory(),
                vendor.getAddress(),
                vendor.getDetailAddress(),
                vendor.getIntroduction(),
                vendor.getImage(),
                vendor.getRate()
        );
    }
}