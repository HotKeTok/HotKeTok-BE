package com.hotketok.repository;

import com.hotketok.domain.House;
import com.hotketok.domain.enums.HouseState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HouseRepository extends JpaRepository<House, Long> {
    List<House> findAllByOwnerIdAndState(Long ownerId, HouseState state);
    Optional<House> findFirstByAddressAndState(String address, HouseState state);
    Optional<House> findByTenantIdOrOwnerId(Long tenantId, Long ownerId);

    // HouseId로 모든 입주민 찾음
    List<House> findAllByHouseId(Long houseId);

    // UserId로 HouseId 찾음
    Optional<House> findFirstByTenantIdOrOwnerId(Long tenantId, Long ownerId);

    // 같은 건물 주민을 모두 찾음
    List<House> findAllByAddressAndDetailAddress(String address, String detailAddress);
}
