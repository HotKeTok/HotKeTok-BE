package com.hotketok.dto.internalApi;

import java.util.List;

public record SendPostRequest(
        Long receiverId,
        Boolean isAnonymous,
        List<String> tags, // 다중 선택 가능
        String silentTime,
        String detailContent
) {}
