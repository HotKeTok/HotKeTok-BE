package com.hotketok.domain;

import com.hotketok.domain.enums.HouseState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "houses")
@NoArgsConstructor
@AllArgsConstructor
public class House {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long houseId;

    private Long tenantId;  // 입주민 ID

    private Long ownerId;   // 집주인 ID

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String detailAddress; // 상세주소 (동/호)

    private String floor; // 층

    private String number; // 호수

    @Enumerated(EnumType.STRING)
    private HouseState state; // 상태 enum (NONE, REGISTERED, TENANT_REQUEST, MATCHED)
}
