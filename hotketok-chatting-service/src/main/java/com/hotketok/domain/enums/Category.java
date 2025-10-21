package com.hotketok.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Category {
    GENERAL_EQUIPMENT("종합설비업체"),
    INTERIOR_REMODELING("인테리어/리모델링 업체"),
    PROFESSIONAL("전문업체");

    private final String koreanName;

    @JsonValue
    public String getKoreanName() {
        return koreanName;
    }
}