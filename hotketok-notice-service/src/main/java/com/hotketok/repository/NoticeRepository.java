package com.hotketok.repository;

import com.hotketok.domain.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    // isFix true 먼저 (내림차순) / date 최신순 (내림차순)
    List<Notice> findByAddressOrderByIsFixDescCreatedAtDesc(String address);
}
