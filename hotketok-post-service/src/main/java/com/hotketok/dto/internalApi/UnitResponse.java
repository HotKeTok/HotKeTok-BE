package com.hotketok.dto.internalApi;

import java.util.List;

public record UnitResponse(
        Long userId,
        String unitNumber,
        List<String> tags
) {}
