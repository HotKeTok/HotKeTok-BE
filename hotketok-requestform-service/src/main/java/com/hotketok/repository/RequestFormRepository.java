package com.hotketok.repository;

import com.hotketok.domain.RequestForm;
import com.hotketok.domain.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface RequestFormRepository extends JpaRepository<RequestForm, Long> {
    // 진행중인 수리요청서 확인
    List<RequestForm> findAllByAddressAndNumberAndStatusNot(String address, String number, Status status);
    List<RequestForm> findAllByAddressAndStatusNot(String address, Status status);
    // 완료된 수리요청서 확인
    List<RequestForm> findAllByAddressAndStatus(String address, Status status);
    List<RequestForm> findAllByAddressAndNumberAndStatus(String address, String number, Status status);

    List<RequestForm> findAllByIdIn(List<Long> requestFormId);
    List<RequestForm> findAllByStatusIn(List<Status> statuses);
    List<RequestForm> findAllByIdInAndRequestScheduleBetween(List<Long> ids, LocalDateTime start, LocalDateTime end);
    List<RequestForm> findAllByAuthorId(Long authorId);
}
