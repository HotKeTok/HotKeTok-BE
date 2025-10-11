package com.hotketok.dto.internalApi;

import com.hotketok.domain.enums.ConstructCategory;

public record RequestFormListResponse(Long requestFormId, String address, ConstructCategory category) {}