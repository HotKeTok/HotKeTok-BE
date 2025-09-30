package com.hotketok.service;

import com.hotketok.domain.House;
import com.hotketok.domain.HouseTag;
import com.hotketok.domain.enums.HouseState;
import com.hotketok.dto.*;
import com.hotketok.dto.internalApi.*;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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

    // 내부 통신 API (쪽지에서 집 정보를 가져오기 위함)
    public HouseInfoResponse findHouseInfoByUserId(Long userId) {
        return houseRepository.findByTenantIdOrOwnerId(userId, userId)
                .map(house -> new HouseInfoResponse(
                        userId,
                        house.getFloor(),
                        house.getNumber(),
                        house.getHouseTags().stream()
                                .map(HouseTag::getContent)
                                .collect(Collectors.toList())
                ))
                .orElseThrow(() -> new IllegalArgumentException("해당 유저의 집 정보를 찾을 수 없습니다. userId=" + userId));
    }

    // 내부 통신 API (userId로 유저의 houseId 가져오기 위함)
    public HouseIdResponse findHouseIdByUserId(Long userId) {
        return houseRepository.findByTenantIdOrOwnerId(userId, userId)
                .map(house -> {
                    log.info(">>>> [HouseService] 조회된 houseId: {}", house.getHouseId());
                    return new HouseIdResponse(house.getHouseId());
                })
                .orElseGet(() -> {
                    log.warn(">>>> [HouseService] userId에 해당하는 집을 찾지 못했습니다: userId={}", userId);
                    return new HouseIdResponse(null);
                });
    }

    // 내부 통신 API (userId로 같은 주택에 사는 입주민 정보를 가져오기 위함)
    public List<HouseInfoResponse> findResidentsByHouseId(Long houseId) {
        return houseRepository.findById(houseId).map(baseHouse -> {
            String address = baseHouse.getAddress();
            String detailAddress = baseHouse.getDetailAddress();

            List<House> residents = houseRepository.findAllByAddressAndDetailAddress(address, detailAddress);

            List<HouseInfoResponse> responseList = residents.stream()
                    .map(resident -> new HouseInfoResponse(
                            resident.getTenantId() != null ? resident.getTenantId() : resident.getOwnerId(),
                            resident.getFloor(),
                            resident.getNumber(),
                            // 태그 다중 반환
                            resident.getHouseTags().stream()
                                    .map(HouseTag::getContent)
                                    .collect(Collectors.toList())
                    ))
                    .collect(Collectors.toList());

            log.info("최종 반환될 주민 정보 DTO 목록: {}", responseList);
            return responseList;

        }).orElseGet(() -> {
            log.warn("houseId에 해당하는 기준 집을 찾지 못했습니다: houseId={}", houseId);
            return Collections.emptyList(); // 기준 집이 없으면 빈 리스트를 반환
        });
    }

    @Transactional(readOnly = true)
    public GetHouseInfoByAddressResponse getHouseInfoByAddress(Long userId, String role, String address) {
        House house = null;
        if (role.equals("OWNER")){
            house = houseRepository.findFirstByAddressAndOwnerId(address, userId).orElseThrow(() -> new CustomException(HouseErrorCode.HOUSE_NOT_FOUND));
        }else if(role.equals("TENANT")){
            house = houseRepository.findFirstByAddressAndTenantId(address, userId).orElseThrow(() -> new CustomException(HouseErrorCode.HOUSE_NOT_FOUND));
        }
        return new GetHouseInfoByAddressResponse(address,house.getNumber(),house.getState().toString());
    }
}

