package com.hotketok.domain;

import com.hotketok.domain.enums.Category;
import com.hotketok.domain.enums.VendorState;
import com.hotketok.hotketokjpaservice.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "vendor")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Vendor extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vendor_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VendorState state; // NONE, REGISTERED

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category; // GENERAL_EQUIPMENT, INTERIOR_REMODELING, PROFESSIONAL

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String detailAddress;

    @Column
    private String introduction;

    @Column(nullable = false)
    private String image;

    @Column(nullable = false)
    private String proveFile;

    @Column
    private int rate;

    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<News> newsList;

    @Builder(access = AccessLevel.PROTECTED)
    private Vendor(Long userId,
                   String name,
                   VendorState state,
                   Category category,
                   String address,
                   String detailAddress,
                   String introduction,
                   String image,
                   String proveFile,
                   int rate) {
        this.userId = userId;
        this.name = name;
        this.state = state;
        this.category = category;
        this.address = address;
        this.detailAddress = detailAddress;
        this.introduction = introduction;
        this.image = image;
        this.proveFile = proveFile;
        this.rate = rate;
    }

    public static Vendor createVendor(Long userId,
                                      String name,
                                      Category category,
                                      String address,
                                      String detailAddress,
                                      String introduction,
                                      String image,
                                      String proveFile) {
        return Vendor.builder()
                .userId(userId)
                .name(name)
                .state(VendorState.NONE)
                .category(category)
                .address(address)
                .detailAddress(detailAddress)
                .introduction(introduction)
                .image(image)
                .proveFile(proveFile)
                .rate(0)
                .build();
    }

    public void changeState(VendorState state) {
        this.state = state;
    }
}
