package com.hotketok.internalApi;

import com.hotketok.dto.SignUpRequest;
import com.hotketok.dto.UserInfo;
import com.hotketok.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/save")
    public void save(@RequestBody SignUpRequest req){ userService.save(req); }

    @GetMapping("/find-by-logInId")
    public UserInfo findByLogInId(@RequestParam String logInId){ return userService.findByLogInId(logInId); }

    @GetMapping("/find-by-id/{id}")
    public UserInfo findById(@PathVariable Long id){ return userService.findById(id); }

}
