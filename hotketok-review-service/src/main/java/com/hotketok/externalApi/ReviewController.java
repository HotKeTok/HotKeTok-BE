package com.hotketok.externalApi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotketok.dto.CreateReviewRequest;
import com.hotketok.dto.ReviewListResponse;
import com.hotketok.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/review-service")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final ObjectMapper objectMapper;

    // 리뷰 작성
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void createReview(
            @RequestPart("request") String requestJson,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) throws IOException {
        Long userId = 101L;
        CreateReviewRequest request = objectMapper.readValue(requestJson, CreateReviewRequest.class);
        reviewService.createReview(userId, request, images);
    }

    // 업체별 후기 목록 조회 (토큰 사용 x)
    @GetMapping
    public ReviewListResponse getReviewsByVendorId(@RequestParam Long vendorId) {
        return reviewService.getReviewsByVendorId(vendorId);
    }

    @DeleteMapping
    public void deleteReview(@RequestParam Long reviewId) {
        Long userId = 101L;
        reviewService.deleteReview(userId, reviewId);
    }
}