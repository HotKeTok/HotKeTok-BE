package com.hotketok.dto;

import java.util.List;

public record ReviewListResponse(
        long count,
        List<ReviewItemResponse> reviews
) {
}