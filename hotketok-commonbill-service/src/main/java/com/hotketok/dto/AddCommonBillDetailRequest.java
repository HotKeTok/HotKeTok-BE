package com.hotketok.dto;

import com.hotketok.domain.enums.BillType;

import java.time.LocalDate;

public record AddCommonBillDetailRequest(
        String description,
        Long amount,
        BillType type,
        LocalDate date ) {
}
