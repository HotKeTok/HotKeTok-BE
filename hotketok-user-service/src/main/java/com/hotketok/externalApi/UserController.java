package com.hotketok.externalApi;

import com.hotketok.dto.MyPageInfoResponse;
import com.hotketok.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/user-service")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/mypage")
    public MyPageInfoResponse getMyPageProfile(@RequestHeader("userId") Long userId, @RequestHeader("role") String role) {
        return userService.GetMyPageInfo(userId, role);
    }
}
