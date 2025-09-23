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

@RestController
@RequestMapping("/internal/users")
@RequiredArgsConstructor
public class UserInternalController {

    private final UserService userService;
    @PostMapping("/profiles")
    public List<UserProfileResponse> getUserProfilesByIds(@RequestBody List<Long> userIds) {
        return userService.findUserProfilesByIds(userIds);
    }
}
