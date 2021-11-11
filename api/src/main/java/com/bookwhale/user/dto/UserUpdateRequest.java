package com.bookwhale.user.dto;

import com.bookwhale.user.domain.User;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserUpdateRequest {

  @NotBlank
  private String name;

  @NotBlank
  @Email
  private String email;

  @Builder
  public UserUpdateRequest(String name, String email) {
    this.name = name;
    this.email = email;
  }

  public User toEntity() {
    return User.builder()
        .name(name)
        .email(email)
        .build();
  }
}
