package com.hotketok.dto.internalApi;

import java.util.List;

public record EstimateChatInfoResponse(
        Long roomId,
        List<String> imageUrls
) {}