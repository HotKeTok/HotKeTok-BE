package com.hotketok.dto;

import com.hotketok.domain.Review;
import com.hotketok.domain.ReviewImage;
import com.hotketok.dto.internalApi.UserProfileResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record ReviewItemResponse(
        Long reviewId,
        int rate,
        String category,
        String content,
        String writerName,
        String writerProfileImage,
        LocalDateTime date,
        List<String> reviewImage,
        Long authorId
) {
    public static ReviewItemResponse of(Review review, UserProfileResponse writerProfile) {
        String name = (writerProfile != null) ? writerProfile.userName() : "알 수 없는 사용자";
        String image = (writerProfile != null) ? writerProfile.profileImageUrl() : null;

        List<String> imageUrls = review.getReviewImages().stream()
                .map(ReviewImage::getImageUrl)
                .collect(Collectors.toList());

        return new ReviewItemResponse(
                review.getId(),
                review.getRate(),
                review.getConstructCategory().name(),
                review.getReview(),
                name,
                image,
                review.getCreatedAt(),
                imageUrls,
                review.getUserId()
        );
    }
}