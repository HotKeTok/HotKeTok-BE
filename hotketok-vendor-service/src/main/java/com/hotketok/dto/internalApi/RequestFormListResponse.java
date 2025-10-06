package com.hotketok.dto.internalApi;

import com.hotketok.domain.enums.Category;

public record RequestFormListResponse(Long requestFormId, String address, Category category) {}