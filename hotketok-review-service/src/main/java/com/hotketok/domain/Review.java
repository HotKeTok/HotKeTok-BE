package com.hotketok.domain;

import com.hotketok.domain.enums.Category;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    private List<String> reviewImage;

    @Builder
    private Review(Long userId, Long vendorId, Category constructCategory, int rate, String review, List<String> reviewImage) {
        this.userId = userId;
        this.vendorId = vendorId;
        this.constructCategory = constructCategory;
        this.rate = rate;
        this.review = review;
        this.reviewImage = reviewImage;
    }

    public static Review createReview(Long userId, Long vendorId, Category constructCategory, int rate, String review, List<String> reviewImage) {
        return Review.builder().userId(userId).vendorId(vendorId).constructCategory(constructCategory).rate(rate).review(review).reviewImage(reviewImage).build();
    }
}