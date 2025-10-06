package com.hotketok.dto;

import com.hotketok.domain.enums.Status;

import java.time.LocalDateTime;

public record VendorEstimateResponse(Long estimateId, com.hotketok.domain.enums.ConstructCategory category, String address, LocalDateTime estimateTime, Status status) {}