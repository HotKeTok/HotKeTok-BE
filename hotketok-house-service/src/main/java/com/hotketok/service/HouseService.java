package com.hotketok.service;

import com.hotketok.domain.House;
import com.hotketok.domain.HouseTag;
import com.hotketok.domain.enums.ChatRoomType;
import com.hotketok.domain.enums.HouseState;
import com.hotketok.dto.*;
import com.hotketok.dto.internalApi.*;
import com.hotketok.exception.HouseErrorCode;
import com.hotketok.hotketokcommonservice.error.exception.CustomException;
import com.hotketok.internalApi.ChatServiceClient;
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
    private final ChatServiceClient chatServiceClient;
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
        userServiceClient.updateOnboardingStageFlag(ownerId, true);
        return new RegisterHouseResponse(registeredHouses);
    }

    // 관리자 승인 → OWNER로 승격
    @Transactional
    public void approveHouse(List<Long> houseId) {
        List<House> house = houseRepository.findAllById(houseId);
        if (house.isEmpty()) {
            throw new CustomException(HouseErrorCode.HOUSE_NOT_FOUND);
        }
        house.forEach(h -> h.changeState(HouseState.REGISTERED));
        userServiceClient.updateRole(house.get(0).getOwnerId(),Role.OWNER);
        userServiceClient.changeCurrentAddressAndNumberFirst(house.get(0).getOwnerId(), house.get(0).getAddress(), house.get(0).getNumber());
    }

    // 관리자 거절 -> 삭제
    @Transactional
    public void rejectHouse(List<Long> houseId) {
        List<House> house = houseRepository.findAllById(houseId);
        if (house.isEmpty()) {
            throw new CustomException(HouseErrorCode.HOUSE_NOT_FOUND);
        }
        house.forEach(h -> houseRepository.deleteById(h.getHouseId()));
        userServiceClient.updateOnboardingStageFlag(house.get(0).getOwnerId(), false);
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
        userServiceClient.updateOnboardingStageFlag(tenantId, true);
        return new RegisterTenantResponse(tenantId, house.getHouseId());
    }

    // 집주인 요청 목록 조회 (state=2)
    @Transactional
    public List<TenantRequestResponse> getTenantRequestList(Long ownerId) {
        CurrentAddressResponse currentAddress = userServiceClient.getCurrentAddress(ownerId);
        List<House> houseList = houseRepository.findAllByOwnerIdAndAddressAndState(ownerId, currentAddress.currentAddress(), HouseState.TENANT_REQUEST);
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

        Long tenantId = house.getTenantId();
        userServiceClient.updateRole(house.getTenantId(), Role.TENANT);
        userServiceClient.changeCurrentAddressAndNumberFirst(house.getTenantId(), house.getAddress(), house.getNumber());

        // 집주인과 입주민 사이 채팅방 생성
        try {
            List<Long> participantUserIds = List.of(ownerId, tenantId);
            CreateChatRoomRequest chatRequest = new CreateChatRoomRequest(
                    participantUserIds,
                    ChatRoomType.GENERAL,
                    null // 요청서 없음
            );

            Long newRoomId = chatServiceClient.createChatRoom(chatRequest).requestFormId();
            log.info("Successfully created chat room {} for owner {} and tenant {}", newRoomId, ownerId, tenantId);

        } catch (Exception e) {
            log.error("Failed to create chat room for houseId: {}, ownerId: {}, tenantId: {}", houseId, ownerId, tenantId, e);
        }
    }

    // 집주인 거절 -> tenantId null, state=1
    @Transactional
    public void rejectTenant(Long houseId, Long ownerId) {
        House house = houseRepository.findById(houseId).orElseThrow(() -> new CustomException(HouseErrorCode.HOUSE_NOT_FOUND));
        if (!house.getOwnerId().equals(ownerId)) {
            throw new CustomException(HouseErrorCode.HOUSE_NOT_EQUAL_OWNER);
        }
        userServiceClient.updateOnboardingStageFlag(house.getTenantId(), false);
        house.changeTenantId(null);
        house.registerTenant(null, null,null,null);
        house.changeState(HouseState.REGISTERED);
    }

    public HouseInfoResponse findHouseInfoByUserId(Long userId, String currentAddress) {
        return houseRepository.findByTenantIdAndAddress(userId, currentAddress)
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

    public HouseInfoResponse getMatchedHousesByTenantAndAddressNumber(Long userId, String currentAddress, String currentNumber) {
        return houseRepository.findByTenantIdAndAddressAndNumber(userId, currentAddress, currentNumber)
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

    // 마이페이지 사용자 정보 조회 -> 주택 태그들을 가져오기 위한 서비스
    @Transactional(readOnly = true)
    public List<String> getHouseTag(String address, String number) {
        House house = houseRepository.findByAddressAndNumber(address,number).orElseThrow(() -> new CustomException(HouseErrorCode.HOUSE_NOT_FOUND));
        return house.getHouseTags().stream().map(HouseTag::getContent).collect(Collectors.toList());
    }

    // 현재 주소,호수의 집주인 찾기
    @Transactional(readOnly = true)
    public Long getOwnerId(Long userId, String currentAddress, String currentNumber){
        House house = houseRepository.findByAddressAndNumberAndTenantId(currentAddress, currentNumber, userId)
                .orElseThrow(() -> new CustomException(HouseErrorCode.HOUSE_NOT_FOUND));
        return house.getOwnerId();
    }

    // 유저 아이디로 호수 반환
    public List<HouseUnitResponse> findUnitNumbersByUserIds(List<Long> userIds) {
        List<House> houses = houseRepository.findAllByTenantIdIn(userIds);
        return houses.stream()
                .map(house -> new HouseUnitResponse(house.getTenantId(), house.getNumber(), house.getAddress()))
                .collect(Collectors.toList());
    }

    // 마이페이지 사용자가 등록한(요청 포함) 주택 정보 제공하는 기능
    @Transactional(readOnly = true)
    public List<MyPageHouseInfoResponse> findHouseInfoListByUserId(Long userId , String role) {
        List<House> houseList;
        List<MyPageHouseInfoResponse> result;

        CurrentAddressAndNumberResponse currentAddressAndNumber = userServiceClient.getCurrentAddressAndNumber(userId);

        if (role.equals("OWNER")){ // 집주인 인 경우
            houseList = houseRepository.findAllByOwnerId(userId);
            List<House> distinctByAddress = houseList.stream()
                    .collect(Collectors.collectingAndThen(
                            Collectors.toMap(
                                    House::getAddress, // key: address
                                    h -> h,            // value: house 객체
                                    (existing, replacement) -> existing // 중복시 기존 값 유지
                            ),
                            m -> new ArrayList<>(m.values())
                    ));

            result = distinctByAddress.stream().distinct().map(h -> new MyPageHouseInfoResponse(
                    h.getAddress(),
                    h.getDetailAddress(), // 집주인 인 경우 동호수 대신 상세주소로 반환
                    null,
                    null,
                    null,
                    h.getState(),
                    h.getAddress().equals(currentAddressAndNumber.currentAddress())
            )).toList();
            return result;

        } else{ // 입주민 인 경우
            houseList = houseRepository.findAllByTenantId(userId);
            result = houseList.stream().distinct().map(h -> new MyPageHouseInfoResponse(
                    h.getAddress(),
                    h.getNumber(),
                    h.getHouseTags().stream().map(HouseTag::getContent).collect(Collectors.toList()),
                    h.getAlias(),
                    h.getType(),
                    h.getState(),
                    ( h.getAddress().equals(currentAddressAndNumber.currentAddress())
                            && h.getNumber().equals(currentAddressAndNumber.currentNumber()) )
            )).toList();
            return result;
        }
    }

    // 집주인 -> 입주민 정보 조회
    @Transactional(readOnly = true)
    public HouseTenantInfoResponse getHouseTenantInfo(Long userId, String number){
        CurrentAddressResponse currentAddress = userServiceClient.getCurrentAddress(userId);
        House house = houseRepository.findByAddressAndNumber(currentAddress.currentAddress(), number)
                .orElseThrow(() -> new CustomException(HouseErrorCode.HOUSE_NOT_FOUND));

        TenantInfoResponse tenantInfo = userServiceClient.getTenantInfo(house.getTenantId());
        return new HouseTenantInfoResponse(tenantInfo, house.getTenantMemo());
    }

    // 집주인 -> 입주민 메모 수정
    @Transactional
    public void updateHouseTenantInfo(Long userId, ChangeHouseTenantMemoRequest request){
        CurrentAddressResponse currentAddress = userServiceClient.getCurrentAddress(userId);
        House house = houseRepository.findByAddressAndNumber(currentAddress.currentAddress(), request.number())
                .orElseThrow(() -> new CustomException(HouseErrorCode.HOUSE_NOT_FOUND));
        house.changeTenantMemo(request.tenantMemo());
    }

}

