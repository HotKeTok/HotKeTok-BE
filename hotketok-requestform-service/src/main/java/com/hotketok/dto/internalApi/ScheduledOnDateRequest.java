package com.hotketok.dto.internalApi;

import java.util.List;

public record ScheduledOnDateRequest(List<Long> requestFormIds, int year, int month, int day) {}
