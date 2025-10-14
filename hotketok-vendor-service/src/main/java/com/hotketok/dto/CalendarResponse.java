package com.hotketok.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public record CalendarResponse(int year, int month, @JsonProperty("calendar_data") Map<String, List<CalendarItemResponse>> calendarData) {}