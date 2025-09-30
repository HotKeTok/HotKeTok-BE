package com.hotketok.dto.internalApi;

public record GetHouseInfoByAddressResponse(
        String address,
        String number,
        String houseState

) {
}

