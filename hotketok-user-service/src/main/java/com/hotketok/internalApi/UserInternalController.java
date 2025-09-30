package com.hotketok.internalApi;

// 채팅 서비스에서 호출하는 컨트롤러
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
        return userService.findUserProfilesByIds(userIds);
    }

    @PostMapping("/save")
    public void save(@RequestBody SignUpRequest req){ userService.save(req); }

    @GetMapping("/find-by-logInId")
    public UserInfo findByLogInId(@RequestParam String logInId){ return userService.findByLogInId(logInId); }

    @GetMapping("/find-by-id/{id}")
    public UserInfo findById(@PathVariable Long id){ return userService.findById(id); }

    @PostMapping("/change-role/{userId}")
    public void updateRole(@PathVariable("userId") Long userId, @RequestParam("role") Role role){ userService.updateRole(userId, role); };

    @GetMapping("/get-tenantInfo/{userId}")
    public TenantInfoResponse getTenantInfo(@PathVariable("userId") Long userId){ return userService.getTenantInfo(userId);}
}

