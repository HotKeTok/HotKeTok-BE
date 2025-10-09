package com.hotketok.repository;

import com.hotketok.domain.Estimate;
import com.hotketok.domain.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EstimateRepository extends JpaRepository<Estimate, Long> {
    List<Estimate> findAllByRequestFormId(Long requestFormId);
    List<Estimate> findAllByVendorId(Long vendorId);

    List<Estimate> findAllByVendorIdAndStatus(Long vendorId, Status status);
}
