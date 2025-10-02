package com.hotketok.externalApi;

import com.hotketok.dto.CreateReviewRequest;
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

    // 리뷰 작성
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> createReview(@RequestPart("request") CreateReviewRequest request, @RequestPart(value = "images", required = false) List<MultipartFile> images) throws IOException {
        Long userId = 101L;
        reviewService.createReview(userId, request, images);
        return ResponseEntity.ok("리뷰가 성공적으로 작성되었습니다.");
    }
}