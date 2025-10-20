package com.hotketok.domain;

import com.hotketok.domain.enums.Category;
import com.hotketok.domain.enums.VendorState;
import com.hotketok.dto.RunningTimeRequest;
import com.hotketok.hotketokjpaservice.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vendor")
@Getter
@NoArgsConstructor
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

    @Column
    private String image;

    @Column(nullable = false)
    private String proveFile;

    @Column
    private int rate;

    @Column
    private String phoneNumber;

    @Embedded
    private RunningTimeRequest runningTime;

    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<News> newsList;

    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VendorIntroductionImage> introductionImages = new ArrayList<>();

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
                   int rate,
                   String phoneNumber,
                   RunningTimeRequest runningTime) {
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
        this.phoneNumber = phoneNumber;
        this.runningTime = runningTime;
    }

    public static Vendor createVendor(Long userId,
                                      String name,
                                      Category category,
                                      String address,
                                      String detailAddress,
                                      String introduction,
                                      String proveFile) {
        return Vendor.builder()
                .userId(userId)
                .name(name)
                .state(VendorState.NONE)
                .category(category)
                .address(address)
                .detailAddress(detailAddress)
                .introduction(introduction)
                .proveFile(proveFile)
                .rate(0)
                .build();
    }

    public void updateProfile(String introduction, String phoneNumber, RunningTimeRequest runningTime, String image, List<String> introductionImageUrls) {
        if (introduction != null) {
            this.introduction = introduction;
        }
        if (phoneNumber != null) {
            this.phoneNumber = phoneNumber;
        }
        if (runningTime != null) {
            this.runningTime = runningTime;
        }
        if (image != null) {
            this.image = image;
        }
        if (introductionImageUrls != null) {
            this.introductionImages.clear(); // 기존 이미지 목록 지우고
            List<VendorIntroductionImage> newImages = introductionImageUrls.stream()
                    .map(url -> VendorIntroductionImage.builder().imageUrl(url).build())
                    .toList();
            newImages.forEach(this::addIntroductionImage); // 새 이미지 목록을 추가
        }
    }

    public void addIntroductionImage(VendorIntroductionImage image) {
        this.introductionImages.add(image);
        image.setVendor(this);
    }
    public void changeState(VendorState state) {
        this.state = state;
    }

    public void addNews(News news) {
        this.newsList.add(news);
        news.setVendor(this);
    }
}
