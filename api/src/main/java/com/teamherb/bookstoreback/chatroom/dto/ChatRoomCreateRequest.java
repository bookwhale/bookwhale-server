package com.teamherb.bookstoreback.chatroom.dto;

import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChatRoomCreateRequest {

  @NotNull
  private Long postId;

  @NotNull
  private Long sellerId;

  @Builder
  public ChatRoomCreateRequest(Long postId, Long sellerId) {
    this.postId = postId;
    this.sellerId = sellerId;
  }
}
