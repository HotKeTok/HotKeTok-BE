package com.hotketok.exception;

import com.hotketok.hotketokcommonservice.error.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum HouseErrorCode implements ErrorCode {
    HOUSE_NOT_FOUND(HttpStatus.NOT_FOUND, "주택을 찾을 수 없습니다."),

    HOUSE_REGISTERED(HttpStatus.CONFLICT, "이미 존재하는 주택입니다"),

    HOUSE_ALREADY_DELETED(HttpStatus.BAD_REQUEST, "이미 삭제된 주택입니다."),
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

