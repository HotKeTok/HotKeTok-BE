package com.hotketok.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record RunningTimeRequest(
        String openingTime,
        String closingTime,

        @JsonProperty("working_day_of_week")
        List<String> workingDayOfWeek
) {}