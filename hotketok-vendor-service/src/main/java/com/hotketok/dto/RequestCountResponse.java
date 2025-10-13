package com.hotketok.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RequestCountResponse(
        @JsonProperty("new_request") long newRequest,
        @JsonProperty("processing_request") long processingRequest,
        @JsonProperty("done_request") long doneRequest
) {}
