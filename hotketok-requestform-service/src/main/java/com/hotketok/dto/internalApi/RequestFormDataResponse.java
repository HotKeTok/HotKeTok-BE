package com.hotketok.dto.internalApi;
import com.hotketok.domain.enums.ConstructCategory;

public record RequestFormDataResponse(String address, ConstructCategory category) {}