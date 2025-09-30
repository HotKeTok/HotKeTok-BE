package com.hotketok.dto.internalApi;

import java.util.List;

public record HouseInfoResponse(
        Long userId,
        String floor,
        String number,
        List<String> houseTags
) {}