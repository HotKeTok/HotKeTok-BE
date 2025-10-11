package com.hotketok.dto;

import com.hotketok.domain.Vendor;
import com.hotketok.domain.VendorIntroductionImage;
import com.hotketok.domain.enums.Category;
import java.util.List;
import java.util.stream.Collectors;

public record VendorInfoAllResponse(
        Long vendorId,
        String name,
        Category category,
        String addressAndDetail,
        String detailAddress,
        String introduction,
        String image, // 대표 이미지
        List<String> introductionImages, // 소개 사진 목록
        int rate
) {
    public static VendorInfoAllResponse from(Vendor vendor) {
        List<String> imageUrls = vendor.getIntroductionImages().stream()
                .map(VendorIntroductionImage::getImageUrl)
                .collect(Collectors.toList());

        return new VendorInfoAllResponse(
                vendor.getId(),
                vendor.getName(),
                vendor.getCategory(),
                vendor.getAddress(),
                vendor.getDetailAddress(),
                vendor.getIntroduction(),
                vendor.getImage(),
                imageUrls, // 업체 소개 이미지들
                vendor.getRate()
        );
    }
}