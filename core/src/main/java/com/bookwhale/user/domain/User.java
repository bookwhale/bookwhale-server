package com.bookwhale.user.domain;

import com.bookwhale.common.domain.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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

  @Column(nullable = false)
  private String phoneNumber;

  private String profileImage;

  @Enumerated(EnumType.STRING)
  private Role role;

  public String getRoleName() {
    return this.role.name();
  }

  @Builder
  public User(Long id, String identity, String password, String name, String email,
      String phoneNumber, String profileImage, Role role) {
    this.id = id;
    this.identity = identity;
    this.password = password;
    this.name = name;
    this.email = email;
    this.phoneNumber = phoneNumber;
    this.profileImage = profileImage;
    this.role = role;
  }

  public static User create(User user, String encodedPassword) {
    return User.builder()
        .identity(user.getIdentity())
        .password(encodedPassword)
        .email(user.getEmail())
        .name(user.getName())
        .phoneNumber(user.getPhoneNumber())
        .role(Role.ROLE_USER)
        .build();
  }

  public void update(User user) {
    this.name = user.getName();
    this.phoneNumber = user.getPhoneNumber();
    this.email = user.getEmail();
  }

  public void uploadProfile(String profileImage) {
    this.profileImage = profileImage;
  }

  public void deleteProfile() {
    this.profileImage = null;
  }

  public void updatePassword(String encodedNewPassword) {
    this.password = encodedNewPassword;
  }
}
