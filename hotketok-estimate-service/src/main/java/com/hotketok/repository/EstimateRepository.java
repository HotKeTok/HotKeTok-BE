package com.hotketok.repository;

import com.hotketok.domain.Estimate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EstimateRepository extends JpaRepository<Estimate, Long> {
}
