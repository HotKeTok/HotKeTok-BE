package com.hotketok.domain;

import com.hotketok.domain.enums.Role;
import com.hotketok.hotketokjpaservice.entity.BaseTimeEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
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

    @Column
    private String logInId;// 로그인 ID

    @Column
    private String password;

    @Column(unique = true)
    private String phoneNumber;

    @Column(nullable = false)
    private String name;

    @Email
    private String email;

    @Column
    private String profileImage;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Builder(access = AccessLevel.PRIVATE)
    private User(String logInId, String password, String phoneNumber, String name, String email, String profileImage, Role role) {
        this.logInId = logInId;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.email = email;
        this.profileImage = profileImage;
        this.role = role;
    }

    public static User createUser(String logInId, String password, String phoneNumber, String name, String email) {
        return User.builder()
                .logInId(logInId)
                .password(password)
                .phoneNumber(phoneNumber)
                .name(name)
                .email(email)
                .profileImage(null)
                .role(Role.NONE)
                .build();
    }
}
