package com.hotketok.exception;

import com.hotketok.hotketokcommonservice.error.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ChattingErrorCode implements ErrorCode {

    CHAT_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "채팅방을 찾을 수 없습니다."),
    NOT_A_PARTICIPANT(HttpStatus.FORBIDDEN, "채팅방 참여자가 아닙니다.");

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public HttpStatus getStatus() {
        return this.httpStatus;
    }

    @Override
    public String getErrorName() {
        return this.name();
    }
}