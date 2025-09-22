package com.hotketok.domain;

import com.hotketok.domain.enums.Role;
import com.hotketok.domain.enums.SocialType;
import com.hotketok.hotketokjpaservice.entity.BaseTimeEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AccessLevel;
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

    @Enumerated(EnumType.STRING)
    private SocialType socialType;
}
