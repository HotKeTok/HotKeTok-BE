package com.hotketok.dto;

public record ChangeCurrentAddressRequest(
        String currentAddress,
        String currentNumber
) {
}
