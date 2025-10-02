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

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewImage> reviewImages = new ArrayList<>();

    @Builder
    private Review(Long userId, Long vendorId, Category constructCategory, int rate, String review) {
        this.userId = userId;
        this.vendorId = vendorId;
        this.constructCategory = constructCategory;
        this.rate = rate;
        this.review = review;
    }
    public static Review createReview(Long userId, Long vendorId, Category constructCategory, int rate, String review, List<String> imageUrls) {
        Review newReview = Review.builder()
                .userId(userId)
                .vendorId(vendorId)
                .constructCategory(constructCategory)
                .rate(rate)
                .review(review)
                .build();

        if (imageUrls != null) {
            imageUrls.stream()
                    .map(url -> ReviewImage.builder().imageUrl(url).build())
                    .forEach(newReview::addReviewImage);
        }

        return newReview;
    }

    public void addReviewImage(ReviewImage reviewImage) {
        this.reviewImages.add(reviewImage);
        reviewImage.setReview(this);
    }
}