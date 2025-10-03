package com.hotketok.exception;

import com.hotketok.hotketokcommonservice.error.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum VendorErrorCode implements ErrorCode {
    VENDOR_NOT_FOUND(HttpStatus.NOT_FOUND, "공사업체를 찾을 수 없습니다."),

    ALREADY_REGISTERED(HttpStatus.CONFLICT, "이미 존재하는 공사업체입니다"),

    REQUEST_FORM_ALREADY_DELETED(HttpStatus.BAD_REQUEST, "이미 삭제된 공사업체입니다."),
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

