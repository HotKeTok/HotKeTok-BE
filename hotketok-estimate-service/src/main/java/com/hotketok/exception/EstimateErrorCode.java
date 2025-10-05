package com.hotketok.exception;

import com.hotketok.hotketokcommonservice.error.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum EstimateErrorCode implements ErrorCode {
    ESTIMATE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당하는 견적서가 존재하지 않습니다."),
    PRICE_IS_ESSENTIAL(HttpStatus.BAD_REQUEST, "'나중에 결정'이 아닐 경우, 견적 가격은 필수입니다.");

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
