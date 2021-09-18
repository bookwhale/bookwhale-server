package com.teamherb.bookstoreback.chatroom.dto;

import com.teamherb.bookstoreback.chatroom.domain.ChatRoom;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChatRoomResponse {

  private Long chatRoomId;
  private Long postId;
  private String postTitle;
  private String postImage; // 네이버 책 API 썸네일
  private String buyerIdentity;
  private String buyerProfile;
  private String sellerIdentity;
  private String sellerProfile;

  @Builder
  public ChatRoomResponse(Long chatRoomId, Long postId, String postTitle,
      String postImage, String buyerIdentity, String buyerProfile, String sellerIdentity,
      String sellerProfile) {
    this.chatRoomId = chatRoomId;
    this.postId = postId;
    this.postTitle = postTitle;
    this.postImage = postImage;
    this.buyerIdentity = buyerIdentity;
    this.buyerProfile = buyerProfile;
    this.sellerIdentity = sellerIdentity;
    this.sellerProfile = sellerProfile;
  }

  public static List<ChatRoomResponse> listOf(List<ChatRoom> chatRooms) {
    return chatRooms.stream().map(v -> ChatRoomResponse.builder()
        .chatRoomId(v.getId())
        .postId(v.getPost().getId())
        .postTitle(v.getPost().getTitle())
        .postImage(v.getPost().getBook().getBookThumbnail())
        .buyerIdentity(v.getBuyer().getIdentity())
        .buyerProfile(v.getBuyer().getProfileImage())
        .sellerIdentity(v.getSeller().getIdentity())
        .sellerProfile(v.getSeller().getProfileImage())
        .build()).collect(Collectors.toList());
  }
}
