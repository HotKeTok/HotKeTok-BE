package com.hotketok.repository;

import com.hotketok.domain.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VendorRepository extends JpaRepository<Vendor,Long> {
    Optional<Vendor> findByNameAndAddress(String name, String address);

    List<Vendor> findAllByIdIn(List<Long> ids);
}
