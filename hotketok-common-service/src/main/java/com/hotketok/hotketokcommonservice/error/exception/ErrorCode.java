package com.hotketok.hotketokcommonservice.error.exception;

import org.springframework.http.HttpStatus;

public interface ErrorCode {
    HttpStatus getStatus();

    String getMessage();

    default String getErrorName() {
        return this.toString();
    }
}
