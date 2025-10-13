package com.hotketok.dto;

import java.util.List;

public record NewRequestListResponse(long count, List<NewRequestItem> request) {}