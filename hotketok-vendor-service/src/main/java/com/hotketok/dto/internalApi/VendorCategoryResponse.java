package com.hotketok.dto.internalApi;

import com.hotketok.domain.enums.Category;

public record VendorCategoryResponse(
        Long vendorId,
        Category category
) {}
