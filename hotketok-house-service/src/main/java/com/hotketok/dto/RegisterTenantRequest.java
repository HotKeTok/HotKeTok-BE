package com.hotketok.dto;

public record RegisterTenantRequest(
        String address,
        String floor,
        String number
) {
}
