package com.hotketok.dto.internalApi;

import java.util.List;

public record ScheduledRequest(List<Long> requestFormIds, int year, int month) {}
