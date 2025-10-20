package com.hotketok.dto.internalApi;

import java.util.List;

public record FloorResponse(
        String floor,
        List<UnitResponse> units
) {}
