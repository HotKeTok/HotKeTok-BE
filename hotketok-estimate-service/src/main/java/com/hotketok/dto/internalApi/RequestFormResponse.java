package com.hotketok.dto.internalApi;

import com.hotketok.domain.enums.Category;

public record RequestFormResponse (
    String address,
    Category category
) {}
