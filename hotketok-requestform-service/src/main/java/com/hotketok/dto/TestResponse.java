package com.hotketok.dto;

import lombok.Data;

@Data
public class TestResponse {
    String name;

    public TestResponse(String name) {
        this.name = name;
    }
}
