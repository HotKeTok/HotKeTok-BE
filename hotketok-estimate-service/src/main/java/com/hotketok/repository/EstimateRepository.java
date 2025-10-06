package com.hotketok.repository;

import com.hotketok.domain.Estimate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EstimateRepository extends JpaRepository<Estimate, Long> {
    List<Estimate> findAllByRequestFormId(Long requestFormId);
}
