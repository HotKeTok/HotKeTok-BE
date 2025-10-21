package com.hotketok.dto.internalApi;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record RunningTimeResponse(
        String openingTime,
        String closingTime,
        @JsonProperty("working_day_of_week")
        List<String> workingDayOfWeek
) {
}