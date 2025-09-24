package com.hotketok.externalApi;

import com.hotketok.dto.*;
import com.hotketok.service.AuthService;
import com.hotketok.service.PhoneAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth-service")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final PhoneAuthService phoneAuthService;

    @PostMapping("/signup")
    public SignUpResponse signup(@RequestBody SignUpRequest req){
        System.out.println("[AuthController] /signup: " + req.logInId());
        return authService.signup(req);
    }

    @PostMapping("/phone/send")
    public SendPhoneAuthResponse send(@RequestParam String phone){
        return phoneAuthService.sendCode(phone);
    }

    @PostMapping("/phone/verify")
    public VerifyPhoneAuthResponse verify(@RequestParam String phone, @RequestParam String code) {
        return phoneAuthService.verify(phone, code);
    }

    @PostMapping("/login")
    public JwtToken login(@RequestBody LoginRequest req){
        System.out.println("[AuthController] /login: " + req.logInId());
        return authService.login(req);
    }

    @PostMapping("/token/refresh")
    public JwtToken refresh(@RequestBody RefreshTokenRequest refreshTokenRequest){
        System.out.println("[AuthController] /token/refresh");
        return authService.refresh(refreshTokenRequest.refreshToken());
    }
}

