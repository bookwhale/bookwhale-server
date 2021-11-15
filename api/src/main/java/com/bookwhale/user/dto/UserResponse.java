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
  private String profileImage;
  private String nickname;

  @Builder
  public UserResponse(Long userId, String identity, String name, String email, String profileImage,
      String nickname) {
    this.userId = userId;
    this.identity = identity;
    this.name = name;
    this.email = email;
    this.profileImage = profileImage;
    this.nickname = nickname;
  }

  public static UserResponse of(User user) {
    return UserResponse.builder()
        .userId(user.getId())
        .identity(user.getIdentity())
        .name(user.getName())
        .email(user.getEmail())
        .profileImage(user.getProfileImage())
        .nickname(user.getNickname())
        .build();
  }
}
