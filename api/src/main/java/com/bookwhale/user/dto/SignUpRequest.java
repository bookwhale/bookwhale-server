package com.bookwhale.user.dto;

import com.bookwhale.user.domain.Role;
import com.bookwhale.user.domain.User;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SignUpRequest {

  @NotBlank
  private String identity;

  @NotBlank
  private String password;

  @NotBlank
  private String name;

  @NotBlank
  @Email
  private String email;

  @Builder
  public SignUpRequest(String identity, String password, String name, String email) {
    this.identity = identity;
    this.password = password;
    this.name = name;
    this.email = email;
  }

  public User toEntity() {
    return User.builder()
        .identity(identity)
        .email(email)
        .name(name)
        .role(Role.ROLE_USER)
        .build();
  }
}
