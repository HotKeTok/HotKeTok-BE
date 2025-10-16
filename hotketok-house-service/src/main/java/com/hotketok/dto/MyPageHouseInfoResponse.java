package com.hotketok.dto;

import com.hotketok.domain.enums.HouseState;
import com.hotketok.domain.enums.HouseType;

import java.util.List;

public record MyPageHouseInfoResponse(
        String address,
        String number,
        List<String> houseTags,
        String alias,
        HouseType type,
        HouseState state,
        boolean isCurrent
) {
}
