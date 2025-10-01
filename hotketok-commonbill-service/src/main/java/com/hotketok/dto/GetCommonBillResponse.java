package com.hotketok.dto;

import com.hotketok.domain.CommonBill;
import com.hotketok.domain.enums.BillType;

import java.time.LocalDate;
import java.util.List;

public record GetCommonBillResponse(
        int year,
        int month,
        Long income,
        Long expense,
        Long balance,
        List<GetCommonBillDetailResponse> details
) {
    public record GetCommonBillDetailResponse(
            String description,
            Long amount,
            BillType type,
            LocalDate date
    ) {
    }

    public static GetCommonBillResponse from(CommonBill bill) {
        List<GetCommonBillDetailResponse> detailBills = bill.getDetails().stream().map(d ->
                new GetCommonBillDetailResponse(d.getDescription(), d.getAmount(), d.getType(), d.getDate())
        ).toList();
        return new GetCommonBillResponse(bill.getYear(), bill.getMonth(),
                bill.getTotalIncome(), bill.getTotalExpense(), bill.getBalance(), detailBills);
    }
}
