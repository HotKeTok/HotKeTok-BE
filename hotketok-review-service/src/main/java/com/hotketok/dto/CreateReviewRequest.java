package com.hotketok.dto;
import com.hotketok.domain.enums.Category;
public record CreateReviewRequest(Long vendorId, Category construct_category, int rate, String review) {}