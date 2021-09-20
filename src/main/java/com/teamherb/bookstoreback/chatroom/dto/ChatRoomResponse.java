package com.teamherb.bookstoreback.chatroom.dto;

import com.teamherb.bookstoreback.chatroom.domain.ChatRoom;
import com.teamherb.bookstoreback.chatroom.domain.Opponent;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChatRoomResponse {

  private Long roomId;
  private Long postId;
  private String postTitle;
  private String postBookThumbnail;
  private String opponentIdentity;
  private String opponentProfile;
  private boolean isOpponentLeave;

  @Builder
  public ChatRoomResponse(Long roomId, Long postId, String postTitle,
      String postBookThumbnail, String opponentIdentity, String opponentProfile,
      boolean isOpponentLeave) {
    this.roomId = roomId;
    this.postId = postId;
    this.postTitle = postTitle;
    this.postBookThumbnail = postBookThumbnail;
    this.opponentIdentity = opponentIdentity;
    this.opponentProfile = opponentProfile;
    this.isOpponentLeave = isOpponentLeave;
  }

  public static ChatRoomResponse of(ChatRoom chatRoom, Opponent opponent,
      boolean isOpponentLeave) {
    return ChatRoomResponse.builder()
        .roomId(chatRoom.getId())
        .postId(chatRoom.getPost().getId())
        .postTitle(chatRoom.getPost().getTitle())
        .opponentIdentity(opponent.getIdentity())
        .opponentProfile(opponent.getProfile())
        .isOpponentLeave(isOpponentLeave)
        .build();
  }
}
