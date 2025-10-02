package com.hotketok.dto;

import com.hotketok.domain.Review;
import com.hotketok.dto.internalApi.UserProfileResponse;

public record ReviewItemResponse(
        Long reviewId,
        int rate,
        String category,
        String content,
        String writerName,
        String writerProfileImage
) {
    public static ReviewItemResponse of(Review review, UserProfileResponse writerProfile) {
        String name = (writerProfile != null) ? writerProfile.userName() : "알 수 없는 사용자";
        String image = (writerProfile != null) ? writerProfile.profileImageUrl() : null;

        return new ReviewItemResponse(
                review.getId(),
                review.getRate(),
                review.getConstructCategory().name(),
                review.getReview(),
                name,
                image
        );
    }
}