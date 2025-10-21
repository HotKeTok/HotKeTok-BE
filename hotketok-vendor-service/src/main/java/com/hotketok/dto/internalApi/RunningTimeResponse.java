package com.hotketok.dto.internalApi;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hotketok.domain.RunningTime;
import java.util.List;

public record RunningTimeResponse(
        String openingTime,
        String closingTime,
        @JsonProperty("working_day_of_week")
        List<String> workingDayOfWeek
) {
    public static RunningTimeResponse from(RunningTime runningTime) {
        if (runningTime == null) {
            return null;
        }
        return new RunningTimeResponse(
                runningTime.getOpeningTime(),
                runningTime.getClosingTime(),
                runningTime.getWorkingDayOfWeek()
        );
    }
}