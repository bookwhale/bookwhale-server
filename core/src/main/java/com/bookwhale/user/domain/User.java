package com.bookwhale.user.domain;

import com.bookwhale.common.domain.BaseEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nickname;

    private String profileImage;

    @Column(nullable = false)
    private String email;

    @Column
    private String deviceToken;

    @Builder
    protected User(Long id, String nickname, String profileImage, String email,
        String deviceToken) {
        this.id = id;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.email = email;
        this.deviceToken = deviceToken;
    }

    public static User create(User user) {
        return User.builder()
            .nickname(user.getNickname())
            .email(user.getEmail())
            .profileImage(user.getProfileImage())
            .deviceToken(user.getDeviceToken())
            .build();
    }

    public void updateUserName(String name) {
        this.nickname = name;
    }

    public void updateUserDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public void uploadProfile(String profileImage) {
        this.profileImage = profileImage;
    }

    public void deleteProfile() {
        this.profileImage = null;
    }

    public void convertUnavailableUser(String hashedEmail) {
        this.nickname = "** 탈퇴한 사용자 **";
        this.email = hashedEmail;
        deleteProfile();
    }
}
