package com.hotketok.dto;

import java.util.List;

public record MatchingEstimateListResponse(long count, List<MatchingEstimateInfoResponse> requestForm) {}
