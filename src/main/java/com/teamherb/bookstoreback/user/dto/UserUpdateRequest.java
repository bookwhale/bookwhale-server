package com.teamherb.bookstoreback.user.dto;

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
  private String phoneNumber;

  @NotBlank
  @Email
  private String email;

  @Builder
  public UserUpdateRequest(String name, String phoneNumber, String email) {
    this.name = name;
    this.phoneNumber = phoneNumber;
    this.email = email;
  }
}
