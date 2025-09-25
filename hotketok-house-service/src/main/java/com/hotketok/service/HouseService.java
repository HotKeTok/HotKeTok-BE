package com.hotketok.service;

import com.hotketok.domain.House;
import com.hotketok.domain.enums.HouseState;
import com.hotketok.dto.*;
import com.hotketok.dto.internalApi.Role;
import com.hotketok.dto.internalApi.UploadFileResponse;
import com.hotketok.exception.HouseErrorCode;
import com.hotketok.hotketokcommonservice.error.exception.CustomException;
import com.hotketok.internalApi.InfraServiceClient;
import com.hotketok.internalApi.UserServiceClient;
import com.hotketok.repository.HouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HouseService {
    private final HouseRepository houseRepository;
    private final UserServiceClient userServiceClient;
    private final InfraServiceClient infraServiceClient;
    // 집주인 등록 (state=0)
    @Transactional
    public RegisterHouseResponse registerHouse(Long ownerId, MultipartFile file, RegisterHouseRequest request) {
        UploadFileResponse uploadFileResponse = infraServiceClient.uploadFile(file,"proveHouse/");

        List<Long> registeredHouses = new ArrayList<>();
        for (int i = 0 ; i < request.count(); i++) {
            House house = House.createHouse(ownerId, request.address(),request.detailAddress(),null,null,null, uploadFileResponse.fileUrl(),request.houseType());
            houseRepository.save(house);
            registeredHouses.add(house.getHouseId());
        }
        return new RegisterHouseResponse(registeredHouses);
    }

    // 관리자 승인 → OWNER로 승격
    @Transactional
    public void approveHouse(Long houseId) {
        House house = houseRepository.findById(houseId).orElseThrow(() -> new CustomException(HouseErrorCode.HOUSE_NOT_FOUND));
        house.changeState(HouseState.REGISTERED);

        userServiceClient.updateRole(house.getOwnerId(), Role.OWNER);
    }

    // 관리자 거절 -> 삭제
    @Transactional
    public void rejectHouse(Long houseId) {
        houseRepository.deleteById(houseId);
    }

    // 입주민 요청 -> 주소/동/호수로 검색해서 state=2
    @Transactional
    public RegisterTenantResponse registerTenant(Long tenantId, RegisterTenantRequest registerTenantRequest) {
        House house = houseRepository.findFirstByAddressAndState(registerTenantRequest.address(), HouseState.REGISTERED)
                .orElseThrow(() -> new CustomException(HouseErrorCode.HOUSE_NOT_FOUND));

        if (house.getState() != HouseState.REGISTERED) {
            throw new CustomException(HouseErrorCode.HOUSE_STATE_NOT_EQUAL_REGISTERED);
        }

        house.changeTenantId(tenantId);
        house.registerTenant(registerTenantRequest.floor(), registerTenantRequest.number());
        house.changeState(HouseState.TENANT_REQUEST);
        return new RegisterTenantResponse(tenantId, house.getHouseId());
    }

    // 집주인 요청 목록 조회 (state=2)
    @Transactional
    public List<TenantRequestResponse> getTenantRequestList(Long ownerId) {
        List<House> houseList = houseRepository.findAllByOwnerIdAndState(ownerId, HouseState.TENANT_REQUEST);
        List<TenantRequestResponse> response = houseList.stream()
                .map(house -> TenantRequestResponse.of(house.getHouseId(),userServiceClient.getTenantInfo(house.getTenantId()), house.getNumber()))
                .toList();
        return response;
    }

    // 집주인 승인 → TENANT로 승격
    @Transactional
    public void approveTenant(Long houseId, Long ownerId) {
        House house = houseRepository.findById(houseId).orElseThrow(() -> new CustomException(HouseErrorCode.HOUSE_NOT_FOUND));
        if (!house.getOwnerId().equals(ownerId)) {
            throw new CustomException(HouseErrorCode.HOUSE_NOT_EQUAL_OWNER);
        }
        if (house.getState() != HouseState.TENANT_REQUEST) {
            throw new CustomException(HouseErrorCode.HOUSE_STATE_NOT_EQUAL_TENANT_REQUEST);
        }
        house.changeState(HouseState.MATCHED);

        userServiceClient.updateRole(house.getTenantId(), Role.TENANT);
    }

    // 집주인 거절 -> tenantId null, state=1
    @Transactional
    public void rejectTenant(Long houseId, Long ownerId) {
        House house = houseRepository.findById(houseId).orElseThrow(() -> new CustomException(HouseErrorCode.HOUSE_NOT_FOUND));
        if (!house.getOwnerId().equals(ownerId)) {
            throw new CustomException(HouseErrorCode.HOUSE_NOT_EQUAL_OWNER);
        }
        house.changeTenantId(null);
        house.registerTenant(null, null);
        house.changeState(HouseState.REGISTERED);
    }
}

