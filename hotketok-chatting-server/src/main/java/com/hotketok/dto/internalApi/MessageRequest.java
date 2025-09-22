package com.hotketok.dto.internalApi;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MessageRequest {
    private Long roomId;
    private Long senderId;
    private String content;
}
