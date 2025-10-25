package com.hotketok.dto;

import com.hotketok.domain.Review;
import com.hotketok.domain.ReviewImage;
import com.hotketok.dto.internalApi.UserProfileResponse;
import com.hotketok.dto.internalApi.VendorProfileResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record ReviewHouseResponse(
        Long reviewId,
        int rate,
        String category,
        String content,
        String writerName,
        String writerProfileImage,
        LocalDateTime date,
        List<String> reviewImage,
        Long vendorId,
        String vendorName,
        String vendorProfileImage
) {
    public static ReviewHouseResponse of(Review review, UserProfileResponse writerProfile, VendorProfileResponse vendorProfile) {
        String name = (writerProfile != null) ? writerProfile.userName() : "알 수 없는 사용자";
        String image = (writerProfile != null) ? writerProfile.profileImageUrl() : null;

        List<String> imageUrls = review.getReviewImages().stream()
                .map(ReviewImage::getImageUrl)
                .collect(Collectors.toList());

        String vendorName = (vendorProfile != null) ? vendorProfile.vendorName() : "알 수 없는 공사업체";
        String vendorImage = (vendorProfile != null) ? vendorProfile.vendorProfileImage() : null;

        return new ReviewHouseResponse(
                review.getId(),
                review.getRate(),
                review.getConstructCategory().name(),
                review.getReview(),
                name,
                image,
                review.getCreatedAt(),
                imageUrls,
                vendorProfile.vendorId(),
                vendorName,
                vendorImage
        );
    }
}