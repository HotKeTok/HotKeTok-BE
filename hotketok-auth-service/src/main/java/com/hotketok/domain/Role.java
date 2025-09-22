package com.hotketok.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Role {
    NONE("NONE"), TENANT("TENANT"), OWNER("OWNER");
    String value;
}
