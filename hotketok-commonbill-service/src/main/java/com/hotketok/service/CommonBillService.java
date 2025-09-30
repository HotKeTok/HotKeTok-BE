package com.hotketok.service;

import com.hotketok.domain.CommonBill;
import com.hotketok.domain.CommonBillDetail;
import com.hotketok.dto.AddCommonBillDetailRequest;
import com.hotketok.dto.GetCommonBillResponse;
import com.hotketok.exception.CommonBillErrorCode;
import com.hotketok.hotketokcommonservice.error.exception.CustomException;
import com.hotketok.internalApi.UserServiceClient;
import com.hotketok.repository.CommonBillDetailRepository;
import com.hotketok.repository.CommonBillRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommonBillService {
    private final CommonBillRepository commonBillRepository;
    private final CommonBillDetailRepository commonBillDetailRepository;
    private final UserServiceClient userServiceClient;

    // 집주인이 공통관리비 항목 추가
    @Transactional
    public void addCommonBillDetail(Long ownerId, AddCommonBillDetailRequest request) {
        String address = userServiceClient.getCurrentAddress(ownerId).currentAddress();

        CommonBill commonBill = commonBillRepository.findByAddressAndYearAndMonth(address,
                        request.date().getYear(),
                        request.date().getMonthValue())
                .orElseGet(() -> commonBillRepository.save(
                        CommonBill.createCommonBill(address, request.date().getYear(), request.date().getMonthValue()))
                );

        CommonBillDetail detail = CommonBillDetail.createCommonBillDetail(commonBill,request.description(),request.amount(),request.type(),request.date());
        commonBillDetailRepository.save(detail);

        commonBill.getDetails().add(detail);
        commonBill.recalculate();

        commonBillRepository.save(commonBill);
    }

    // 입주민/집주인 조회용: 해당 월의 공통관리비 내역
    public GetCommonBillResponse getCommonBills(Long userId, int year, int month) {
        String address = userServiceClient.getCurrentAddress(userId).currentAddress();

        CommonBill commonBill = commonBillRepository.findByAddressAndYearAndMonth(address, year, month)
                .orElseThrow(() -> new CustomException(CommonBillErrorCode.COMMON_BILL_NOT_FOUND));

        return GetCommonBillResponse.from(commonBill);
    }
}

