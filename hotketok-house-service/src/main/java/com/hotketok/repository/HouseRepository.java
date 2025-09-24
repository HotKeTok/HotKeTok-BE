package com.hotketok.repository;

import com.hotketok.domain.House;
import com.hotketok.domain.enums.HouseState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HouseRepository extends JpaRepository<House, Long> {
    List<House> findAllByOwnerIdAndState(Long ownerId, HouseState state);
}
