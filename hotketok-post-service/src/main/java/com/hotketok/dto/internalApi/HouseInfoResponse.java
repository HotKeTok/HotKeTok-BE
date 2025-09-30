package com.hotketok.dto.internalApi;

public record HouseInfoResponse(
    Long userId,
    String floor,
    String number
) {
}