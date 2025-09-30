package com.hotketok.domain;

import com.hotketok.domain.enums.BillType;
import com.hotketok.hotketokjpaservice.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "common_bill_details")
@Getter
@NoArgsConstructor
@AllArgsConstructor
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
}