package com.hotketok.exception;

import com.hotketok.hotketokcommonservice.error.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum RequestFormErrorCode implements ErrorCode {
    REQUEST_FORM_NOT_FOUND(HttpStatus.NOT_FOUND, "요청서를 찾을 수 없습니다."),

    ALREADY_REGISTERED(HttpStatus.CONFLICT, "이미 존재하는 요청서입니다"),

    REQUEST_FORM_ALREADY_DELETED(HttpStatus.BAD_REQUEST, "이미 삭제된 요청서입니다."),

    REQUEST_FORM_CREATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "요청서 생성에 실패했습니다."),
    CHAT_SERVICE_UNAVAILABLE(HttpStatus.INTERNAL_SERVER_ERROR, "채팅 메시지 사용불가합니다."),
    CHAT_ROOM_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "채팅 방을 찾을 수 없습니다.")
    ;

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
