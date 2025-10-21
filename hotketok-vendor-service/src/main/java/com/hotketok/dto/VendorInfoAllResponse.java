package com.hotketok.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotketok.domain.RunningTime;
import com.hotketok.domain.Vendor;
import com.hotketok.domain.VendorIntroductionImage;
import com.hotketok.domain.enums.Category;
import com.hotketok.dto.internalApi.RunningTimeResponse;

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
        List<String> introductionImage, // 소개 사진 목록
        int rate,
        int reviewCount,
        RunningTimeResponse runningTime,
        String phoneNumber
) {
    public static VendorInfoAllResponse from(Vendor vendor, int reviewCount) {
        List<String> imageUrls = vendor.getIntroductionImages().stream()
                .map(VendorIntroductionImage::getImageUrl)
                .collect(Collectors.toList());

        RunningTime runningTimeEntity = vendor.getRunningTime();
        RunningTimeResponse runningTimeDto = RunningTimeResponse.from(runningTimeEntity);

        return new VendorInfoAllResponse(
                vendor.getId(),
                vendor.getName(),
                vendor.getCategory(),
                vendor.getAddress(),
                vendor.getDetailAddress(),
                vendor.getIntroduction(),
                vendor.getImage(),
                imageUrls, // 업체 소개 이미지들
                vendor.getRate(),
                reviewCount,
                runningTimeDto,
                vendor.getPhoneNumber()
        );
    }
}