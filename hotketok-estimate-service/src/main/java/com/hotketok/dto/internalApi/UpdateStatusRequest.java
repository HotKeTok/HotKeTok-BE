package com.hotketok.dto.internalApi;
import com.hotketok.domain.enums.Status;

public record UpdateStatusRequest(Status status) {}