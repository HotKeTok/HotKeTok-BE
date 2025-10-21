package com.hotketok.externalApi;

import com.hotketok.dto.ChangeCurrentAddressRequest;
import com.hotketok.dto.MyPageInfoResponse;
import com.hotketok.dto.UpdateMyPageInfoRequest;
import com.hotketok.dto.internalApi.CurrentAddressAndNumberResponse;
import com.hotketok.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/user-service")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/mypage/info")
    public MyPageInfoResponse getMyPageProfile(@RequestHeader("userId") Long userId, @RequestHeader("role") String role) {
        return userService.GetMyPageInfo(userId, role);
    }

    @PostMapping(value = "/mypage/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updateMyPageProfile(
            @RequestHeader("userId") Long userId,
            @RequestPart(value = "data", required = false) UpdateMyPageInfoRequest updateMyPageInfoRequest,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        userService.UpdateMyPageInfo(userId,image,updateMyPageInfoRequest);
        return ResponseEntity.ok().build();
    }

    @PutMapping(value = "/change/current-address-and-number")
    public CurrentAddressAndNumberResponse changeCurrentAddressAndNumber(
            @RequestHeader("userId") Long userId,
            @RequestBody ChangeCurrentAddressRequest request
    ){
        return userService.updateCurrentAddressAndNumber(userId, request.currentAddress(), request.currentNumber());
    }

    @GetMapping(value = "/get/current-address-and-number")
    public CurrentAddressAndNumberResponse getCurrentAddressAndNumber(@RequestHeader("userId") Long userId){
        return userService.getCurrentAddressAndNumberByUserId(userId);
    }
}
