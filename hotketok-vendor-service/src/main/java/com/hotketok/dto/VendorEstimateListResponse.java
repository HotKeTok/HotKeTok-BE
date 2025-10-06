package com.hotketok.dto;

import java.util.List;

public record VendorEstimateListResponse(long count, List<VendorEstimateResponse> estimates) {}