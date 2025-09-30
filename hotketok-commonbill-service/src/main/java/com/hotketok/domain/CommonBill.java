package com.hotketok.domain;

import com.hotketok.domain.enums.BillType;
import com.hotketok.hotketokjpaservice.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    private Long ownerId;

    private int year;

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
}

