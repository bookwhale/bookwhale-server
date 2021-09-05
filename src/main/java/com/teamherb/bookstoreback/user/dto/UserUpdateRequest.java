package com.teamherb.bookstoreback.user.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserUpdateRequest {

  private String name;
  private String phoneNumber;
  private String email;

  @Builder
  public UserUpdateRequest(String name, String phoneNumber, String email) {
    this.name = name;
    this.phoneNumber = phoneNumber;
    this.email = email;
  }
}
