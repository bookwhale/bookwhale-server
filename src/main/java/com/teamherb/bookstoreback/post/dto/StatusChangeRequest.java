package com.teamherb.bookstoreback.post.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StatusChangeRequest {

  private Long id;

  private String Status;
  @Builder
  public StatusChangeRequest(Long id, String status) {
    this.id = id;
    Status = status;
  }
}
