package com.hotketok.internalApi;

// 채팅 서비스에서 호출하는 컨트롤러
import com.hotketok.dto.internalApi.CurrentAddressAndNumberResponse;
import com.hotketok.dto.internalApi.CurrentAddressResponse;
import com.hotketok.dto.internalApi.UserInfoDetailResponse;
import com.hotketok.dto.internalApi.UserProfileResponse;
import com.hotketok.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import com.hotketok.domain.enums.Role;
import com.hotketok.dto.SignUpRequest;
import com.hotketok.dto.TenantInfoResponse;
import com.hotketok.dto.UserInfo;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/user-service")
@RequiredArgsConstructor
public class UserInternalController {

    private final UserService userService;

    @PostMapping("/profiles")
    public List<UserProfileResponse> getUserProfilesByIds(@RequestBody List<Long> userIds) {
        return userService.findUserInfoByIds(userIds);
    }

    @GetMapping("/profiles/{userId}")
    public UserProfileResponse getUserProfileById(@PathVariable Long userId) {
        return userService.findUserProfileById(userId);
    }

    @PostMapping("/save")
    public void save(@RequestBody SignUpRequest req){ userService.save(req); }

    @GetMapping("/find-by-logInId")
    public UserInfo findByLogInId(@RequestParam String logInId){ return userService.findByLogInId(logInId); }

    @GetMapping("/find-by-id/{id}")
    public UserInfo findById(@PathVariable Long id){ return userService.findById(id); }

    @PostMapping("/change-role/{userId}")
    public void updateRole(@PathVariable("userId") Long userId, @RequestParam("role") Role role){ userService.updateRole(userId, role); };

    @PostMapping("/change/onboarding-stage-flag/{userId}")
    public void updateOnboardingStageFlag(@PathVariable("userId") Long userId, @RequestParam("flag") boolean flag){ userService.updateOnboardingStageFlag(userId, flag);}

    @GetMapping("/get-tenantInfo/{userId}")
    public TenantInfoResponse getTenantInfo(@PathVariable("userId") Long userId){ return userService.getTenantInfo(userId);}

    @GetMapping("/{userId}/current-address")
    public CurrentAddressResponse getCurrentAddress(@PathVariable Long userId) {
        return userService.getCurrentAddressByUserId(userId);
    }

    @GetMapping ("/current-address-and-number/{userId}")
    public CurrentAddressAndNumberResponse getCurrentAddressAndNumber(@PathVariable Long userId) {
        return userService.getCurrentAddressAndNumberByUserId(userId);
    }

    @PostMapping(value = "/change/current-address-and-number/first/{userId}")
    public void changeCurrentAddressAndNumberFirst(@PathVariable("userId") Long userId,
                                                   @RequestParam("address") String address,
                                                   @RequestParam(value = "number", required = false) String number) {
        userService.updateCurrentAddressAndNumberFirst(userId, address, number);
    }


    @PostMapping(value = "/change/current-address-and-number/{userId}")
    public void changeCurrentAddressAndNumber(
            @PathVariable("userId") Long userId,
            @RequestParam("address") String address,
            @RequestParam("number") String number
    ){
        userService.updateCurrentAddressAndNumber(userId, address, number);
    }

    @PostMapping("/profiles-detail")
    public List<UserInfoDetailResponse> getUserInfosByIds(@RequestBody List<Long> userIds) {
        return userService.findUserInfosByIds(userIds);
    }

    @GetMapping("/profiles-detail/{userId}")
    public UserInfoDetailResponse getUserInfoById(@PathVariable Long userId) {
        return userService.findUserInfoById(userId);
    }

    @GetMapping("/residents-by-address")
    public List<Long> getUserIdsByAddress(@RequestParam("address") String address) {
        return userService.getUserIdsByAddress(address);
    }
}

