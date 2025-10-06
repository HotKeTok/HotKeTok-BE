package com.hotketok.dto.internalApi;
import com.hotketok.domain.enums.Category;

public record RequestFormDataResponse(String address, Category category) {}