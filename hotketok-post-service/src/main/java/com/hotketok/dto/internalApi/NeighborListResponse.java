package com.hotketok.dto.internalApi;

import java.util.List;

public record NeighborListResponse(
        Long currentUserId,
        List<FloorResponse> floors
) {}
