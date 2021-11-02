package com.bookwhale.user.dto;

import com.bookwhale.user.domain.User;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserResponse {

  private Long userId;
  private String identity;
  private String name;
  private String email;
  private String phoneNumber;
  private String profileImage;

  @Builder
  public UserResponse(Long userId, String identity, String name, String email, String phoneNumber,
      String profileImage) {
    this.userId = userId;
    this.identity = identity;
    this.name = name;
    this.email = email;
    this.phoneNumber = phoneNumber;
    this.profileImage = profileImage;
  }

  public static UserResponse of(User user) {
    return UserResponse.builder()
        .userId(user.getId())
        .identity(user.getIdentity())
        .name(user.getName())
        .email(user.getEmail())
        .phoneNumber(user.getPhoneNumber())
        .profileImage(user.getProfileImage())
        .build();
  }
}
