package com.hotketok.domain;

import com.hotketok.domain.enums.BillType;
import com.hotketok.hotketokjpaservice.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "common_bill_details")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommonBillDetail extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "common_bill_id")
    private CommonBill commonBill;

    private String description;

    private Long amount;

    @Enumerated(EnumType.STRING)
    private BillType type;

    private LocalDate date;

    @Builder(access = AccessLevel.PRIVATE)
    private CommonBillDetail(CommonBill commonBill, String description ,Long amount, BillType type, LocalDate date){
        this.commonBill = commonBill;
        this.description = description;
        this.amount = amount;
        this.type = type;
        this.date = date;
    }

    public static CommonBillDetail createCommonBillDetail(CommonBill commonBill, String description, Long amount, BillType type, LocalDate date){
        return CommonBillDetail.builder()
                .commonBill(commonBill)
                .description(description)
                .amount(amount)
                .type(type)
                .date(date)
                .build();
    }
}