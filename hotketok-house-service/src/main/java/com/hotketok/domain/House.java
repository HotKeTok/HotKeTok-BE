package com.hotketok.domain;

import com.hotketok.domain.enums.HouseState;
import com.hotketok.domain.enums.HouseType;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

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
    private String detailAddress; // 상세주소

    private String floor; // 층

    private String number; // 호수

    private String alias; // 별칭

    private String proveFile; // 등기부등본 같은 증명자료

    @Enumerated(EnumType.STRING)
    private HouseState state; // 상태 enum (NONE, REGISTERED, TENANT_REQUEST, MATCHED)

    @Enumerated(EnumType.STRING)
    private HouseType type; // 상태 enum (HOME, COMPANY, ETC)

    @Builder(access = AccessLevel.PRIVATE)
    private House(Long tenantId, Long ownerId, String address, String detailAddress, String floor, String number,String alias, String houseTag, String proveFile, HouseState state, HouseType type) {
        this.tenantId = tenantId;
        this.ownerId = ownerId;
        this.address = address;
        this.detailAddress = detailAddress;
        this.floor = floor;
        this.number = number;
        this.alias = alias;
        this.state = state;
        this.type = type;
        this.proveFile = proveFile;
    }

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "house_id")
    private Set<HouseTag> houseTags = new HashSet<>();

    public static House createHouse(Long ownerId, String address, String detailAddress, String floor, String number,String alias, String proveFile,HouseType type) {
        return House.builder()
                .tenantId(null)
                .ownerId(ownerId)
                .address(address)
                .detailAddress(detailAddress)
                .floor(floor)
                .number(number)
                .alias(alias)
                .state(HouseState.NONE)
                .type(type)
                .proveFile(proveFile)
                .build();
    }

    public void addHouseTag(HouseTag tag) { this.houseTags.add(tag); }
    public void changeState(HouseState state) {
        this.state = state;
    }

    public void changeTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public void registerTenant(String floor, String number) {
        this.floor = floor;
        this.number = number;
    }
}
