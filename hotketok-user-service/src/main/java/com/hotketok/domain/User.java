package com.hotketok.domain;

import com.hotketok.domain.enums.Role;
import com.hotketok.hotketokjpaservice.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String logInId;// 로그인 ID

    @Column(nullable = false)
    private String password;

    @Column(unique = true)
    private String phoneNumber;

    @Column(nullable = false)
    private String name;

    @Column
    private String profileImage;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String currentAddress;

    private String currentNumber;

    @Builder(access = AccessLevel.PRIVATE)
    private User(String logInId, String password, String phoneNumber, String name, String profileImage, Role role, String currentAddress, String currentNumber) {
        this.logInId = logInId;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.profileImage = profileImage;
        this.role = role;
        this.currentAddress = currentAddress;
        this.currentNumber = currentNumber;
    }

    public static User createUser(String logInId, String password, String phoneNumber, String name) {
        return User.builder()
                .logInId(logInId)
                .password(password)
                .phoneNumber(phoneNumber)
                .name(name)
                .profileImage(null)
                .role(Role.NONE)
                .currentAddress(null)
                .currentNumber(null)
                .build();
    }

    public void changeRole(Role role) {
        this.role = role;
    }

    public void changeProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public void changeName(String name) {
        this.name = name;
    }

    public void changeCurrentAddressAndNumber(String currentAddress, String currentNumber) {
        this.currentAddress = currentAddress;
        this.currentNumber = currentNumber;
    }
}
