package com.hotketok.repository;

import com.hotketok.domain.CommonBill;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CommonBillRepository extends JpaRepository<CommonBill, Long> {
    Optional<CommonBill> findByAddressAndYearAndMonth(String address, int year, int month);
}
