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
  private String postImage;
  private String opponentIdentity;
  private String opponentProfile;
  private boolean isOpponentDelete;

  @Builder
  public ChatRoomResponse(Long roomId, Long postId, String postTitle, String postImage,
      String opponentIdentity, String opponentProfile, boolean isOpponentDelete) {
    this.roomId = roomId;
    this.postId = postId;
    this.postTitle = postTitle;
    this.postImage = postImage;
    this.opponentIdentity = opponentIdentity;
    this.opponentProfile = opponentProfile;
    this.isOpponentDelete = isOpponentDelete;
  }

  public static ChatRoomResponse of(ChatRoom chatRoom, Opponent opponent,
      boolean isOpponentDelete) {
    return ChatRoomResponse.builder()
        .roomId(chatRoom.getId())
        .postId(chatRoom.getPost().getId())
        .postTitle(chatRoom.getPost().getTitle())
        .postImage(chatRoom.getPost().getImages().getFirstImageUrl())
        .opponentIdentity(opponent.getIdentity())
        .opponentProfile(opponent.getProfile())
        .isOpponentDelete(isOpponentDelete)
        .build();
  }
}
