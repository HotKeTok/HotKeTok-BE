package com.hotketok.dto;

import com.hotketok.domain.enums.Category;

public record RegisterVendorRequest(
        String name,
        Category category,
        String address,
        String detailAddress,
        String introduction
) {
}
