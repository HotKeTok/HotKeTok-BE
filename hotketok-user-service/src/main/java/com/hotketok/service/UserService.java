package com.hotketok.service;

import com.hotketok.dto.MyPageInfoResponse;
import com.hotketok.dto.internalApi.MyPageHouseInfoResponse;
import com.hotketok.dto.internalApi.UserProfileResponse;
import com.hotketok.internalApi.HouseServiceClient;
import com.hotketok.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final HouseServiceClient houseServiceClient;
  
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

    @Transactional(readOnly = true)
    public MyPageInfoResponse GetMyPageInfo(Long userId, String role){
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
        if (role.equals("NONE")) return new MyPageInfoResponse(user.getName(),user.getPhoneNumber(),user.getLogInId(),null);

        MyPageHouseInfoResponse houseInfo = houseServiceClient.getMyPageHouseInfo(userId,role);
        return new MyPageInfoResponse(user.getName(), user.getPhoneNumber(), user.getLogInId(), houseInfo.address());
    }

    private UserInfo toDto(User u){
        return UserInfo.of(u.getId(),u.getLogInId(),u.getPassword(),u.getRole());
    }

    // 채팅 서비스에서 호출하는 여러 ID를 받아 사용자를 목록을 조회하는 메서드
    public List<UserProfileResponse> findUserProfilesByIds(List<Long> userIds) {
        return userRepository.findAllByIdIn(userIds).stream()
                .map(UserProfileResponse::from)
                .collect(Collectors.toList());
    }
}
