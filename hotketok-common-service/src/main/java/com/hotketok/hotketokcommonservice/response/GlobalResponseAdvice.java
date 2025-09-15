package com.hotketok.hotketokcommonservice.response;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice(basePackages = "com.hotketok.externalApi")
public class GlobalResponseAdvice implements ResponseBodyAdvice {
    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(
            Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class selectedConverterType,
            ServerHttpRequest request,
            ServerHttpResponse response) {

        HttpServletResponse servletResponse =
                ((ServletServerHttpResponse) response).getServletResponse();
        // Spring의 ServerHttpResponse는 추상적이기 때문에,
        //실제 서블릿 응답 객체를 얻으려면 캐스팅이 필요

        int status = servletResponse.getStatus();
        // 응답의 HTTP 상태 코드를 가져옴

        HttpStatus resolve = HttpStatus.resolve(status);
        // 숫자 상태 코드 → HttpStatus Enum으로 변환 (HttpStatus.valueOf(status)의 안전한 버전)

        if (resolve == null || body instanceof String) {
            return body;
        } // 1. 상태 코드가 잘못됐거나, body가 String이면 응답을 가공하지 않음

        if (resolve.is2xxSuccessful()) {
            return GlobalResponse.success(status, body);
        } // 2. 2xx 응답이면 GlobalResponse로 감싸서 응답

        return body;
    }
}