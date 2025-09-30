package com.hotketok.dto;

import com.hotketok.domain.enums.HouseType;

public record RegisterTenantRequest(
        String address,
        String floor,
        String number,
        String alias,
        HouseType houseType
) {
}
