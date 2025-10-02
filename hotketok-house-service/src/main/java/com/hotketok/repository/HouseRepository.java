package com.hotketok.repository;

import com.hotketok.domain.House;
import com.hotketok.domain.enums.HouseState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HouseRepository extends JpaRepository<House, Long> {
    List<House> findAllByOwnerIdAndState(Long ownerId, HouseState state);
    Optional<House> findFirstByAddressAndState(String address, HouseState state);
    Optional<House> findByTenantId(Long tenantId);
    List<House> findAllByAddressAndStateAndTenantId(String address, HouseState state, Long tenantId);

    // 같은 건물 주민을 모두 찾음
    List<House> findAllByAddressAndState(String address, HouseState state);

    Optional<House> findByAddressAndNumberAndTenantId(String address,String number,Long tenantId);
    Optional<House> findFirstByAddressAndOwnerId(String address, Long ownerId);
}
