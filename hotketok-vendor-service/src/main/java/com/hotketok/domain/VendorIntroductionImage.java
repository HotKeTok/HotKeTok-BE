package com.hotketok.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "vendor_introduction_images")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VendorIntroductionImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vendor_image_id")
    private Long id;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id")
    private Vendor vendor;

    @Builder
    private VendorIntroductionImage(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setVendor(Vendor vendor) {
        this.vendor = vendor;
    }
}