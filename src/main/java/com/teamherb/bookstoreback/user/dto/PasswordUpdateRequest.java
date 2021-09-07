package com.teamherb.bookstoreback.user.dto;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordUpdateRequest {

  @NotBlank
  private String oldPassword;

  @NotBlank
  private String newPassword;
}
