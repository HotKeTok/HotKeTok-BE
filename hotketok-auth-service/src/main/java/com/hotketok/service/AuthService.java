package com.hotketok.service;

import com.hotketok.dto.*;
import com.hotketok.exception.AuthErrorCode;
import com.hotketok.hotketokcommonservice.error.exception.CustomException;
import com.hotketok.internalApi.UserServiceClient;
import com.hotketok.repository.PhoneAuthRepository;
import com.hotketok.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserServiceClient userServiceClient;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final PhoneAuthRepository phoneAuthRepository;

    public SignUpResponse signup(SignUpRequest req){
        System.out.println("[AuthService] signup: " + req.logInId());
        // 1) 휴대폰 인증 여부 확인
        if (!phoneAuthRepository.isVerified(req.phoneNumber())) {
            throw new CustomException(AuthErrorCode.UNAUTHORIZED_PHONE_NUMBER);
        }

        // 2) ID 유효성 검증
        if (req.logInId() == null || userServiceClient.findByLogInId(req.logInId()) != null ) {
            throw new CustomException(AuthErrorCode.BAD_REQUEST_LOGINID);
        }
        // 3) PW 유효성 검증
        if (req.password() == null || req.password().length() < 8) {
            throw new CustomException(AuthErrorCode.FORBIDDEN_PASSWORD);
        }
        SignUpRequest createRequest = new SignUpRequest(
                req.name(),
                req.logInId(),
                passwordEncoder.encode(req.password()),
                req.phoneNumber());
        userServiceClient.save(createRequest);
        return new SignUpResponse(req.name());
    }

    public JwtToken login(LoginRequest req){
        log.info("[AuthService] login: " + req.logInId());
        UserInfo user = userServiceClient.findByLogInId(req.logInId());
        if(user==null) throw new CustomException(AuthErrorCode.USER_NOT_FOUNT);
        if(!passwordEncoder.matches(req.password(), user.password()))
            throw new CustomException(AuthErrorCode.BAD_REQUEST_PASSWORD);
        return jwtUtil.issue(user.id(), user.role());
    }
}
