package com.hotketok.externalApi;

import com.hotketok.dto.RegisterHouseRequest;
import com.hotketok.dto.RegisterHouseResponse;
import com.hotketok.service.HouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/houses-service")
@RequiredArgsConstructor
public class HouseController {
    private final HouseService houseService;

    // 집주인 집 등록
    @PostMapping("/register")
    public RegisterHouseResponse registerHouse(/*@RequestHeader("userId")*/ @RequestParam Long ownerId,
                                                                            @RequestBody List<RegisterHouseRequest> requests) {
        return houseService.registerHouse(ownerId, requests);
    }

}

