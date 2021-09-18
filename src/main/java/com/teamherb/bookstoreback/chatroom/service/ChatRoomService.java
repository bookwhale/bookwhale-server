package com.teamherb.bookstoreback.chatroom.service;

import com.teamherb.bookstoreback.chatroom.domain.ChatRoom;
import com.teamherb.bookstoreback.chatroom.domain.ChatRoomRepository;
import com.teamherb.bookstoreback.chatroom.dto.ChatRoomResponse;
import com.teamherb.bookstoreback.user.domain.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatRoomService {

  private final ChatRoomRepository chatRoomRepository;

  @Transactional(readOnly = true)
  public List<ChatRoomResponse> findChatRooms(User loginUser) {
    List<ChatRoom> chatRooms = chatRoomRepository
        .findAllByBuyerOrSellerCreatedDateDesc(loginUser);
    return ChatRoomResponse.listOf(chatRooms);
  }

  public void deleteChatRoom(User loginUser) {

  }
}
