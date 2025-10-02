package com.hotketok.service;

import com.hotketok.dto.*;
import com.hotketok.dto.internalApi.GetHouseInfoByAddressResponse;
import com.hotketok.dto.internalApi.MyPageHouseInfoResponse;
import com.hotketok.dto.internalApi.UploadFileResponse;
import com.hotketok.dto.internalApi.UserProfileResponse;
import com.hotketok.internalApi.HouseServiceClient;
import com.hotketok.internalApi.InfraServiceClient;
import com.hotketok.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.hotketok.domain.User;
import com.hotketok.domain.enums.Role;
import com.hotketok.exception.UserErrorCode;
import com.hotketok.hotketokcommonservice.error.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final HouseServiceClient houseServiceClient;
    private final InfraServiceClient infraServiceClient;
  
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

    @Transactional
    public void updateCurrentAddressAndNumber(Long id, String updateAddress, String updateNumber){
        User user = userRepository.findById(id).orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
        GetHouseInfoByAddressResponse response;
        if (user.getRole().equals(Role.OWNER)){
            response = houseServiceClient.getHouseInfoByAddress(id, user.getRole().name(), updateAddress, updateNumber);
            if (response.houseState().equals("NONE")){
                throw new CustomException(UserErrorCode.CANT_CHANGE_CURRENT_ADDRESS);
            }
        } else if(user.getRole().equals(Role.TENANT)){
            response = houseServiceClient.getHouseInfoByAddress(id,user.getRole().name(),updateAddress, updateNumber);
            if (response.houseState().equals("TENANT_REQUEST")){
                throw new CustomException(UserErrorCode.CANT_CHANGE_CURRENT_ADDRESS);
            }
        } else if(user.getRole().equals(Role.NONE)){
            throw new CustomException(UserErrorCode.CANT_CHANGE_CURRENT_ADDRESS);
        }

        user.changeCurrentAddressAndNumber(updateAddress, updateNumber);
    }

    @Transactional(readOnly = true)
    public MyPageInfoResponse GetMyPageInfo(Long userId, String role){
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
        if (role.equals("NONE")) return new MyPageInfoResponse(user.getName(),user.getPhoneNumber(),user.getLogInId(),null);
        return new MyPageInfoResponse(user.getName(), user.getPhoneNumber(), user.getLogInId(), user.getCurrentAddress());
    }

    @Transactional
    public void UpdateMyPageInfo(Long userId, MultipartFile image, UpdateMyPageInfoRequest updateMyPageInfoRequest){
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
        UploadFileResponse uploadFileResponse = infraServiceClient.uploadFile(image, "user-profile/");
        user.changeProfileImage(uploadFileResponse.fileUrl());
        user.changeName(updateMyPageInfoRequest.name());
    }

    private UserInfo toDto(User u){
        return UserInfo.of(u.getId(),u.getLogInId(),u.getPassword(),u.getRole());
    }

    public List<UserProfileResponse> findUserProfilesByIds(List<Long> userIds) {
        return userRepository.findAllByIdIn(userIds).stream()
                .map(UserProfileResponse::from)
                .collect(Collectors.toList());
    }

    public String getCurrentAddressByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
        return user.getCurrentAddress();
    }

    public UserProfileResponse findUserProfileById(Long userId) {
        return userRepository.findById(userId)
                .map(UserProfileResponse::from)
                .orElseThrow(() -> new RuntimeException("해당하는 사용자가 존재하지 않습니다.")); // 예외 처리
    }
}
