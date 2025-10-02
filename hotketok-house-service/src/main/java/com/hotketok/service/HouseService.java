package com.hotketok.service;

import com.hotketok.domain.House;
import com.hotketok.domain.HouseTag;
import com.hotketok.domain.enums.HouseState;
import com.hotketok.dto.*;
import com.hotketok.dto.internalApi.GetHouseInfoByAddressResponse;
import com.hotketok.dto.internalApi.HouseInfoResponse;
import com.hotketok.dto.internalApi.Role;
import com.hotketok.dto.internalApi.UploadFileResponse;
import com.hotketok.exception.HouseErrorCode;
import com.hotketok.hotketokcommonservice.error.exception.CustomException;
import com.hotketok.internalApi.InfraServiceClient;
import com.hotketok.internalApi.UserServiceClient;
import com.hotketok.repository.HouseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
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
            House house = House.createHouse(ownerId, request.address(),request.detailAddress(), uploadFileResponse.fileUrl());
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
        house.registerTenant(registerTenantRequest.floor(), registerTenantRequest.number(), registerTenantRequest.alias(), registerTenantRequest.houseType());
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
        house.registerTenant(null, null,null,null);
        house.changeState(HouseState.REGISTERED);
    }

    public HouseInfoResponse findHouseInfoByUserId(Long userId) {
        return houseRepository.findByTenantId(userId)
                .map(house -> {
                    List<String> tagContents = house.getHouseTags().stream()
                            .map(HouseTag::getContent)
                            .collect(Collectors.toList());

                    return new HouseInfoResponse(
                            userId,
                            house.getFloor(),
                            house.getNumber(),
                            tagContents
                    );
                })
                .orElseThrow(() -> new CustomException(HouseErrorCode.HOUSE_NOT_FOUND));
    }

    // 내부 통신 API (쪽지에서 집 정보를 가져오기 위함)
    public List<HouseInfoResponse> getMatchedHousesByTenantAndAddress(Long tenantId, String address) {
        List<House> matchedHouses = houseRepository.findAllByAddressAndStateAndTenantId(address, HouseState.MATCHED, tenantId);

        return matchedHouses.stream()
                .map(house -> new HouseInfoResponse(
                        tenantId,
                        house.getFloor(),
                        house.getNumber(),
                        house.getHouseTags().stream()
                                .map(tag -> tag.getContent())
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());
    }

    // 이웃 목록 조회 내부 API
    public List<HouseInfoResponse> findResidentsByAddress(String address) {
        List<House> residents = houseRepository.findAllByAddressAndState(address, HouseState.MATCHED);

        return residents.stream()
                .map(resident -> new HouseInfoResponse(
                        resident.getTenantId() != null ? resident.getTenantId() : resident.getOwnerId(),
                        resident.getFloor(),
                        resident.getNumber(),
                        resident.getHouseTags().stream()
                                .map(HouseTag::getContent)
                                .collect(Collectors.toList())
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public GetHouseInfoByAddressResponse getHouseInfoByAddress(Long userId, String role, String address, String number) {
        House house = null;
        if (role.equals("OWNER")){
            house = houseRepository.findFirstByAddressAndOwnerId(address, userId).orElseThrow(() -> new CustomException(HouseErrorCode.HOUSE_NOT_FOUND));
        }else if(role.equals("TENANT")){
            house = houseRepository.findByAddressAndNumberAndTenantId(address,number,userId).orElseThrow(() -> new CustomException(HouseErrorCode.HOUSE_NOT_FOUND));
        }
        return new GetHouseInfoByAddressResponse(address,number,house.getState().toString());
    }
}

