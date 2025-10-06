package com.hotketok.dto.internalApi;

import com.hotketok.domain.RequestForm;

public record RequestFormAuthorResponse(
        Long authorId
) {
    public static RequestFormAuthorResponse from(RequestForm requestForm) {
        return new RequestFormAuthorResponse(requestForm.getAuthorId());
    }
}