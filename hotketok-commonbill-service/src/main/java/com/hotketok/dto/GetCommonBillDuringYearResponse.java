package com.hotketok.dto;

public record GetCommonBillDuringYearResponse(
        int year,
        int month,
        Long balance
) {
}
