package com.hotketok.service;

import com.hotketok.domain.Review;
import com.hotketok.dto.CreateReviewRequest;
import com.hotketok.dto.UploadFileListResponse;
import com.hotketok.internalApi.InfraServiceClient;
import com.hotketok.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final InfraServiceClient infraServiceClient;

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
}