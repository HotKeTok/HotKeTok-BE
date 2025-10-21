package com.hotketok.dto;

import com.hotketok.domain.enums.ConstructCategory;
import com.hotketok.domain.enums.Status;

import java.time.LocalDateTime;

public record VendorEstimateResponse(Long estimateId, ConstructCategory category, String address, String estimateTime, Status status) {}