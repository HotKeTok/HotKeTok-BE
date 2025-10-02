package com.hotketok.domain;

import com.hotketok.domain.enums.Category;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reviews")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    private Long userId, vendorId;

    @Enumerated(EnumType.STRING)
    private Category constructCategory;

    private int rate;

    @Column(columnDefinition = "TEXT")
    private String review;

    @ElementCollection
    @CollectionTable(name = "review_images", joinColumns = @JoinColumn(name = "review_id"))
    @Column(name = "review_image_url")
    private List<String> reviewImage = new ArrayList<>(); // 빈 리스트로 초기화해서 NPE 방지

    @Builder
    private Review(Long userId, Long vendorId, Category constructCategory, int rate, String review, List<String> reviewImage) {
        this.userId = userId;
        this.vendorId = vendorId;
        this.constructCategory = constructCategory;
        this.rate = rate;
        this.review = review;
        if (reviewImage != null) {
            this.reviewImage = reviewImage;
        } // null 아니면 필드로 교체
    }

    public static Review createReview(Long userId, Long vendorId, Category constructCategory, int rate, String review, List<String> reviewImage) {
        return Review.builder().userId(userId).vendorId(vendorId).constructCategory(constructCategory).rate(rate).review(review).reviewImage(reviewImage).build();
    }
}