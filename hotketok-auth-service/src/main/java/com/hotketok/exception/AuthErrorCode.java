package com.hotketok.exception;

import com.hotketok.hotketokcommonservice.error.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthErrorCode implements ErrorCode {
    UNAUTHORIZED_PHONE_NUMBER(HttpStatus.UNAUTHORIZED, "휴대폰 인증이 필요합니다."),
    NOT_SEND_SMS_SERVICE(HttpStatus.BAD_REQUEST, "SMS 문자가 안보내졌습니다."),

    FORBIDDEN_PASSWORD(HttpStatus.FORBIDDEN, "비밀번호는 8자 이상이어야 합니다."),
    BAD_REQUEST_PASSWORD(HttpStatus.BAD_REQUEST,"비밀번호 불일치"),


    USER_NOT_FOUNT(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."),
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
