package com.hotketok.domain;

import com.hotketok.domain.enums.BillType;
import com.hotketok.hotketokjpaservice.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "common_bills")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommonBill extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String address;

    @Column(name = "bill_year")
    private int year;
    @Column(name = "bill_month")
    private int month;

    private Long totalIncome = 0L;

    private Long totalExpense = 0L;

    private Long balance = 0L;

    @OneToMany(mappedBy = "commonBill", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommonBillDetail> details = new ArrayList<>();

    public void recalculate() {
        totalIncome = details.stream()
                .filter(d -> d.getType() == BillType.INCOME)
                .mapToLong(CommonBillDetail::getAmount).sum();

        totalExpense = details.stream()
                .filter(d -> d.getType() == BillType.EXPENSE)
                .mapToLong(CommonBillDetail::getAmount).sum();

        balance = totalIncome - totalExpense;
    }

    @Builder(access = AccessLevel.PRIVATE)
    private CommonBill(String address, int year, int month, Long totalIncome, Long totalExpense, Long balance) {
        this.address = address;
        this.year = year;
        this.month = month;
        this.totalIncome = totalIncome;
        this.totalExpense = totalExpense;
        this.balance = balance;
    }

    public static CommonBill createCommonBill(String address, int year, int month) {
        return CommonBill.builder()
                .address(address)
                .year(year)
                .month(month)
                .totalIncome(0L)
                .totalExpense(0L)
                .balance(0L)
                .build();
    }
}

