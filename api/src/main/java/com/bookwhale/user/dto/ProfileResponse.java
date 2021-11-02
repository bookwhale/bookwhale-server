package com.bookwhale.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponse {

  private String profileImage;

  public static ProfileResponse of(String imageUrl) {
    return new ProfileResponse(imageUrl);
  }
}
