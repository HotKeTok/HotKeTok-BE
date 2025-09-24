package com.hotketok.service;

import com.hotketok.domain.User;
import com.hotketok.domain.enums.Role;
import com.hotketok.dto.SignUpRequest;
import com.hotketok.dto.TenantInfoResponse;
import com.hotketok.dto.UserInfo;
import com.hotketok.exception.UserErrorCode;
import com.hotketok.hotketokcommonservice.error.exception.CustomException;
import com.hotketok.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public void save(SignUpRequest req){
        log.info("[UserService] create user=" + req.logInId());
        User user = User.createUser(req.logInId(),req.password(),req.phoneNumber(),req.name());
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public UserInfo findByLogInId(String logInId){
        System.out.println("[UserService -> AuthService-signup] findByLogInId=" + logInId);
        return userRepository.findByLogInId(logInId).map(this::toDto).orElse(null);
    }

    @Transactional(readOnly = true)
    public UserInfo findById(Long id){
        System.out.println("[UserService -> AuthService-signup] findById=" + id);
        return userRepository.findById(id).map(this::toDto).orElse(null);
    }

    @Transactional(readOnly = true)
    public TenantInfoResponse getTenantInfo(Long id){
        User user = userRepository.findById(id).orElseThrow(()-> new CustomException(UserErrorCode.USER_NOT_FOUND));
        return TenantInfoResponse.of(user);
    }

    @Transactional
    public void updateRole(Long id, Role role){
        User user = userRepository.findById(id).orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
        user.changeRole(role);
    }

    private UserInfo toDto(User u){
        return UserInfo.of(u.getId(),u.getLogInId(),u.getPassword(),u.getRole());
    }
}

