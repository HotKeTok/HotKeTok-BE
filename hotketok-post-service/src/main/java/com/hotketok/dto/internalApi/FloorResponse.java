package com.hotketok.dto.internalApi;

import java.util.Map;

public record FloorResponse(
        String floor,
        Map<String, String> number
) {
}
