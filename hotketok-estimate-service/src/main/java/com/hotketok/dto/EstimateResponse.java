package com.hotketok.dto;

import com.hotketok.domain.Estimate;
import com.hotketok.dto.internalApi.VendorInfoResponse;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EstimateResponse(
        Long estimateId,
        String vendorName,
        String vendorProfileImage,
        String vendorNumber,
        String content,
        BigDecimal price,
        LocalDateTime estimateTime
) {
    public static EstimateResponse from(Estimate estimate, VendorInfoResponse vendorInfo) {
        String name = (vendorInfo != null) ? vendorInfo.name() : "알 수 없는 업체";
        String image = (vendorInfo != null) ? vendorInfo.image() : null;
        String phone = (vendorInfo != null) ? vendorInfo.vendorNumber() : null;

        return new EstimateResponse(
                estimate.getId(),
                name,
                image,
                phone,
                estimate.getComment(),
                estimate.getEstimatePrice(),
                estimate.getCreatedAt()
        );
    }
}