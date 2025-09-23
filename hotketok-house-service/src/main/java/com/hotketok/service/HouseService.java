package com.hotketok.service;

import com.hotketok.domain.House;
import com.hotketok.dto.RegisterHouseRequest;
import com.hotketok.dto.RegisterHouseResponse;
import com.hotketok.repository.HouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HouseService {
    private final HouseRepository houseRepository;

    // 집주인 등록 (state=0)
    public RegisterHouseResponse registerHouse(Long ownerId, List<RegisterHouseRequest> requests) {
        List<Long> registedHouses = new ArrayList<>();
        for (RegisterHouseRequest req : requests) {
            House house = House.createHouse(ownerId, req.address(),req.detailAddress(),req.floor(),req.number());
            houseRepository.save(house);
            registedHouses.add(house.getHouseId());
        }
        return new RegisterHouseResponse(registedHouses);
    }
}

