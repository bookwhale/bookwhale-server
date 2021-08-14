package com.teamherb.bookstoreback.user.domain;

import com.teamherb.bookstoreback.account.domain.Accounts;
import com.teamherb.bookstoreback.common.domain.BaseEntity;

import javax.persistence.*;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false)
    private String identity;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    private String phoneNumber;

    private String address;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Embedded
    private Accounts accounts = Accounts.empty();

    public String getRoleName() {
        return this.role.name();
    }

    @Builder
    public User(Long id, String identity, String password, String name, String email,
        String phoneNumber, String address, Role role) {
        this.id = id;
        this.identity = identity;
        this.password = password;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.role = role;
    }
}
