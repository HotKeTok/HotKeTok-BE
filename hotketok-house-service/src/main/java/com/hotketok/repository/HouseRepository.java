package com.hotketok.repository;

import com.hotketok.domain.House;
import com.hotketok.domain.enums.HouseState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HouseRepository extends JpaRepository<House, Long> {
    List<House> findAllByOwnerIdAndState(Long ownerId, HouseState state);
    Optional<House> findFirstByAddressAndState(String address, HouseState state);
    Optional<House> findFirstByAddressAndTenantId(String address, Long tenantId);
    Optional<House> findFirstByAddressAndOwnerId(String address, Long ownerId);
}
