package com.hotketok.repository;

import com.hotketok.domain.House;
import com.hotketok.domain.enums.HouseState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HouseRepository extends JpaRepository<House, Long> {
    // 입주민 요청에 대한 집 리스트 반환
    List<House> findAllByOwnerIdAndAddressAndState(Long ownerId, String address, HouseState state);
    Optional<House> findFirstByAddressAndState(String address, HouseState state);
    Optional<House> findByTenantId(Long tenantId);

    List<House> findAllByOwnerId(Long ownerId);
    List<House> findAllByTenantId(Long tenantId);

    List<House> findAllByAddressAndStateAndTenantId(String address, HouseState state, Long tenantId);

    // 같은 건물 주민을 모두 찾음
    List<House> findAllByAddressAndState(String address, HouseState state);

    Optional<House> findByAddressAndNumberAndTenantId(String address,String number,Long tenantId);
    Optional<House> findFirstByAddressAndOwnerId(String address, Long ownerId);
    List<House> findAllByTenantIdIn(List<Long> userIds);

    // // 마이페이지 사용자 정보 조회 -> 주택 태그들을 가져오기 위한 서비스 -> 사용자 주소와 동호수로 주택 태그 찾기
    Optional<House> findByAddressAndNumber(String address, String number);
}
