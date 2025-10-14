package com.hotketok.dto;

import java.util.List;

public record DailyScheduleResponse(
        int year, int month, int day, long count,
        List<DailyScheduleItem> requestForm
) {}
