package com.teamherb.bookstoreback.user.dto;

import javax.validation.constraints.Email;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
public class UserUpdateRequest {

  private String name;

  private String phoneNumber;

  @Email
  private String email;

  @Builder
  public UserUpdateRequest(String name, String phoneNumber, String email) {
    this.name = name;
    this.phoneNumber = phoneNumber;
    this.email = email;
  }
}
