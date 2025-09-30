package com.hotketok.externalApi;

import com.hotketok.dto.ChangeCurrentAddressRequest;
import com.hotketok.dto.MyPageInfoResponse;
import com.hotketok.dto.UpdateMyPageInfoRequest;
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
            @RequestPart(value = "data") UpdateMyPageInfoRequest updateMyPageInfoRequest,
            @RequestPart(value = "image") MultipartFile image) {
        userService.UpdateMyPageInfo(userId,image,updateMyPageInfoRequest);
        return ResponseEntity.ok().build();
    }

    @PutMapping(value = "/change-currentAddress")
    public ResponseEntity<Void> changeCurrentAddress(
            @RequestHeader("userId") Long userId,
            @RequestBody ChangeCurrentAddressRequest request
    ){
        userService.updateCurrentAddress(userId, request.currentAddress());
        return ResponseEntity.ok().build();
    }
}
