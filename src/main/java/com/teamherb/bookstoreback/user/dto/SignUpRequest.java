package com.teamherb.bookstoreback.user.dto;

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

  @NotBlank
  private String phoneNumber;

  @Builder
  public SignUpRequest(String identity, String password, String name, String email,
      String phoneNumber) {
    this.identity = identity;
    this.password = password;
    this.name = name;
    this.email = email;
    this.phoneNumber = phoneNumber;
  }
}
