package com.hotketok.dto;

import com.hotketok.domain.enums.HouseType;

public record RegisterHouseRequest (
        String address,
        String detailAddress,
        Integer count,
        HouseType houseType
){
}
