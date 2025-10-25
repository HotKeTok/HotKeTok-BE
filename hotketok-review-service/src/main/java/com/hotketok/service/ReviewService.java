package com.hotketok.service;

import com.hotketok.domain.Review;
import com.hotketok.domain.ReviewImage; // 👈 ReviewImage import
import com.hotketok.dto.*;
import com.hotketok.dto.internalApi.*;
import com.hotketok.exception.ReviewErrorCode;
import com.hotketok.hotketokcommonservice.error.exception.CustomException;
import com.hotketok.internalApi.EstimateServiceClient;
import com.hotketok.internalApi.InfraServiceClient;
import com.hotketok.internalApi.UserServiceClient;
import com.hotketok.internalApi.VendorServiceClient;
import com.hotketok.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final InfraServiceClient infraServiceClient;
    private final UserServiceClient userServiceClient;
    private final EstimateServiceClient estimateServiceClient;
    private final VendorServiceClient vendorServiceClient;

    // 리뷰 작성
    public Review createReview(Long userId, CreateReviewRequest request, List<MultipartFile> images) {
        log.info("요청받은 이미지 파일 개수: {}", (images != null) ? images.size() : "0");

        List<String> imageUrls = Collections.emptyList();

        if (images != null && !images.isEmpty()) {
            try {
                UploadFileListResponse response = infraServiceClient.uploadImages(images, "reviews");
                imageUrls = response.fileList();
            } catch (Exception e) {
                throw new RuntimeException("이미지 업로드에 실패. 원인: " + e.getMessage());
            }
        } else {
            log.warn("--- 업로드할 이미지가 없음 ---");
        }
        Review review = Review.createReview(
                userId,
                request.vendorId(),
                request.construct_category(),
                request.rate(),
                request.review(),
                imageUrls
        );

        Review savedReview = reviewRepository.save(review);
        log.info("저장된 이미지 URL 개수: {}", savedReview.getReviewImages().size());
        return savedReview;
    }

    // 업체별 리뷰 목록 조회 (토큰 사용 x)
    @Transactional(readOnly = true)
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

    // 리뷰 삭제
    public void deleteReview(Long userId, Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ReviewErrorCode.REVIEW_NOT_FOUND));

        if (!review.getUserId().equals(userId)) {
            throw new CustomException(ReviewErrorCode.NO_AUTHORITY_TO_DELETE);
        }

        // 리뷰에 연결된 이미지 같이 삭제
        List<String> imageUrls = review.getReviewImages().stream()
                .map(ReviewImage::getImageUrl)
                .collect(Collectors.toList());

        if (!imageUrls.isEmpty()) {
            imageUrls.forEach(url -> {
                infraServiceClient.deleteFile(new DeleteFileRequest(url));
            });
        }
        reviewRepository.delete(review);
    }

    public int getReviewCountByVendorId(Long vendorId) {
        long count = reviewRepository.countByVendorId(vendorId);
        return (int) count;
    }

    // 리뷰 작성 가능 여부 확인
    public ReviewStatusResponse statusReview(Long userId, Long vendorId) {
        boolean canWriteReview = estimateServiceClient.hasCompletedWork(userId, vendorId);
        return new ReviewStatusResponse(canWriteReview);
    }

    public ReviewStatsResponse getReviewStatsByVendorId(Long vendorId) {
        List<Review> reviews = reviewRepository.findAllByVendorId(vendorId);
        int reviewCount = reviews.size();
        double averageRate = reviews.stream()
                .mapToDouble(Review::getRate) // 각 리뷰 객체에서 평점(rate)을 double로 추출
                .average() // 평균 계산
                .orElse(0.0); // 리뷰가 없으면 0.0 반환
        return new ReviewStatsResponse(reviewCount, averageRate);
    }

    // 지난 수리 후기 조회
    public List<ReviewHouseResponse> getHouseReview(Long userId) {
        CurrentAddressResponse currentAddressResponse = userServiceClient.getCurrentAddress(userId);
        String currentAddress = currentAddressResponse.currentAddress();

        // userService에 가서 같은 주소에 사는 이들의 입주민 목록을 가져와야 함
        List<Long> residentIds = userServiceClient.getUserIdsByAddress(currentAddress);

        if (residentIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 입주민 목록을 하나씩 돌면서 그들이 작성한 후기들을 가져와서 ReviewHouseResponse 반환
        List<Review> reviews = reviewRepository.findAllByUserIdIn(residentIds);

        if (reviews.isEmpty()) {
            return Collections.emptyList();
        }

        // 리뷰 작성자 프로필
        List<Long> writerIds = reviews.stream()
                .map(Review::getUserId)
                .distinct()
                .toList();

        List<UserProfileResponse> userProfiles = userServiceClient.getUserProfilesByIds(writerIds);
        Map<Long, UserProfileResponse> userProfileMap = userProfiles.stream()
                .collect(Collectors.toMap(UserProfileResponse::userId, profile -> profile));

        // 해당 리뷰의 vendorId로 vendorProfile 가져옴
        List<Long> vendorIds = reviews.stream()
                .map(Review::getVendorId)
                .distinct()
                .toList();

        List<VendorProfileResponse> vendorProfiles = vendorServiceClient.getVendorProfilesByIds(vendorIds);
        Map<Long, VendorProfileResponse> vendorProfileMap = vendorProfiles.stream()
                .collect(Collectors.toMap(VendorProfileResponse::vendorId, profile -> profile));

        List<ReviewHouseResponse> houseReviews = new ArrayList<>();
        for (Review review : reviews) {
            UserProfileResponse writerProfile = userProfileMap.get(review.getUserId());
            VendorProfileResponse vendorProfile = vendorProfileMap.get(review.getVendorId());

            houseReviews.add(ReviewHouseResponse.of(review, writerProfile, vendorProfile));
        }

        return houseReviews;
    }
}