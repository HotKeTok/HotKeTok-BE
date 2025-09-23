package com.hotketok.service;

import com.hotketok.domain.House;
import com.hotketok.domain.enums.HouseState;
import com.hotketok.dto.RegisterHouseRequest;
import com.hotketok.dto.RegisterHouseResponse;
import com.hotketok.dto.internalApi.Role;
import com.hotketok.exception.HouseErrorCode;
import com.hotketok.hotketokcommonservice.error.exception.CustomException;
import com.hotketok.internalApi.UserServiceClient;
import com.hotketok.repository.HouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HouseService {
    private final HouseRepository houseRepository;
    private final UserServiceClient userServiceClient;
    // 집주인 등록 (state=0)
    @Transactional
    public RegisterHouseResponse registerHouse(Long ownerId, List<RegisterHouseRequest> requests) {
        List<Long> registedHouses = new ArrayList<>();
        for (RegisterHouseRequest req : requests) {
            House house = House.createHouse(ownerId, req.address(),req.detailAddress(),req.floor(),req.number());
            houseRepository.save(house);
            registedHouses.add(house.getHouseId());
        }
        return new RegisterHouseResponse(registedHouses);
    }

    @Transactional
    // 관리자 승인 → OWNER로 승격
    public void approveHouse(Long houseId) {
        House house = houseRepository.findById(houseId).orElseThrow(() -> new CustomException(HouseErrorCode.HOUSE_NOT_FOUND));
        house.changeState(HouseState.REGISTERED);

        userServiceClient.updateRole(house.getOwnerId(), Role.OWNER);
    }

    @Transactional
    // 관리자 거절 -> 삭제
    public void rejectHouse(Long houseId) {
        houseRepository.deleteById(houseId);
    }

}

