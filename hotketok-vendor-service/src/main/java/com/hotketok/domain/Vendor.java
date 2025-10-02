package com.hotketok.domain;

import com.hotketok.domain.enums.Category;
import com.hotketok.domain.enums.VendorState;
import com.hotketok.hotketokjpaservice.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

}
