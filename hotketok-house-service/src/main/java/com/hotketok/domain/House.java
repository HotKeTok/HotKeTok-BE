package com.hotketok.domain;

import com.hotketok.domain.enums.HouseState;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "houses")
@Getter
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

    @Builder(access = AccessLevel.PRIVATE)
    private House(Long tenantId, Long ownerId, String address, String detailAddress, String floor, String number, HouseState state) {
        this.tenantId = tenantId;
        this.ownerId = ownerId;
        this.address = address;
        this.detailAddress = detailAddress;
        this.floor = floor;
        this.number = number;
        this.state = state;
    }

    public static House createHouse(Long ownerId, String address, String detailAddress, String floor, String number) {
        return House.builder()
                .tenantId(null)
                .ownerId(ownerId)
                .address(address)
                .detailAddress(detailAddress)
                .floor(floor)
                .number(number)
                .state(HouseState.NONE)
                .build();
    }

    public void changeState(HouseState state) {
        this.state = state;
    }

    public void changeTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

}
