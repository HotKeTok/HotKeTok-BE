package com.hotketok.service;

import com.hotketok.domain.Review;
import com.hotketok.dto.CreateReviewRequest;
import com.hotketok.dto.ReviewItemResponse;
import com.hotketok.dto.ReviewListResponse;
import com.hotketok.dto.UploadFileListResponse;
import com.hotketok.dto.internalApi.UserProfileResponse;
import com.hotketok.internalApi.InfraServiceClient;
import com.hotketok.internalApi.UserServiceClient;
import com.hotketok.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final InfraServiceClient infraServiceClient;
    private final UserServiceClient userServiceClient;

    // 리뷰 작성
    public void createReview(Long userId, CreateReviewRequest request, List<MultipartFile> images) throws IOException {
        List<String> imageUrls = Collections.emptyList();

        // 이미지 요청에 포함되면 infra-service 호출
        if (images != null && !images.isEmpty()) {
            // 'reviews'라는 폴더에 이미지를 저장
            UploadFileListResponse response = infraServiceClient.uploadImages(images, "reviews");
            imageUrls = response.urls();
        }

        Review review = Review.createReview(
                userId,
                request.vendorId(),
                request.construct_category(),
                request.rate(),
                request.review(),
                imageUrls
        );

        reviewRepository.save(review);
    }

    // 업체별 리뷰 목록 조회
    public ReviewListResponse getReviewsByVendorId(Long vendorId) {
        List<Review> reviews = reviewRepository.findAllByVendorId(vendorId);

        if (reviews.isEmpty()) {
            return new ReviewListResponse(0, Collections.emptyList());
        }

        List<Long> writerIds = reviews.stream()
                .map(Review::getUserId)
                .distinct()
                .toList();

        List<UserProfileResponse> userProfiles = userServiceClient.getUserProfilesByIds(writerIds);
        Map<Long, UserProfileResponse> userProfileMap = userProfiles.stream()
                .collect(Collectors.toMap(UserProfileResponse::userId, profile -> profile));
        List<ReviewItemResponse> reviewItems = reviews.stream()
                .map(review -> {
                    UserProfileResponse writerProfile = userProfileMap.get(review.getUserId());
                    return ReviewItemResponse.of(review, writerProfile);
                })
                .collect(Collectors.toList());

        return new ReviewListResponse(reviewItems.size(), reviewItems);
    }
}