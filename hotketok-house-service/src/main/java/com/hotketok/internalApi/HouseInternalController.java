package com.hotketok.internalApi;

import com.hotketok.dto.MyPageHouseInfoResponse;
import com.hotketok.service.HouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/house-service")
@RequiredArgsConstructor
public class HouseInternalController {
    private final HouseService houseService;

    @GetMapping("/mypage/house")
    public MyPageHouseInfoResponse getMyPageHouseInfo(@RequestParam("userId") Long userId, // 예시: Request 객체에 userId가 있다면
                                                      @RequestParam("role") String role) {
        return houseService.getMypageHouseInfo(userId,role);
    }
}
