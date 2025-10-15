package com.hotketok.dto;

import com.hotketok.domain.enums.ConstructCategory;

import java.time.LocalDateTime;

public record RequestFormDateResponse(Long requestId, ConstructCategory category, LocalDateTime estimateTime) {}