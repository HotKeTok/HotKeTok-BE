package com.hotketok.dto;

import com.hotketok.domain.Vendor;
import com.hotketok.domain.VendorIntroductionImage;
import com.hotketok.domain.enums.Category;
import com.hotketok.domain.enums.VendorState;

import java.util.List;
import java.util.stream.Collectors;

public record BeforeRegisterVendorInfoResponse(
        Long vendorId,
        String name,
        Category category,
        String address,
        String detailAddress,
        String introduction,
        List<String> introductionImage,
        VendorState state
        ) {
        public static BeforeRegisterVendorInfoResponse from(Vendor vendor) {
                List<String> imageUrls = vendor.getIntroductionImages().stream()
                        .map(VendorIntroductionImage::getImageUrl)
                        .collect(Collectors.toList());

                return new BeforeRegisterVendorInfoResponse(
                        vendor.getId(),
                        vendor.getName(),
                        vendor.getCategory(),
                        vendor.getAddress(),
                        vendor.getDetailAddress(),
                        vendor.getIntroduction(),
                        imageUrls,
                        vendor.getState()
                );
        }
}
