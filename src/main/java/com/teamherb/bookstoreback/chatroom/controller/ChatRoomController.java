package com.teamherb.bookstoreback.chatroom.controller;

import com.teamherb.bookstoreback.chatroom.dto.ChatRoomResponse;
import com.teamherb.bookstoreback.chatroom.service.ChatRoomService;
import com.teamherb.bookstoreback.security.CurrentUser;
import com.teamherb.bookstoreback.user.domain.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat-room")
@RequiredArgsConstructor
public class ChatRoomController {

  private final ChatRoomService chatRoomService;

  //나의 채팅방 리스트
  @GetMapping
  public ResponseEntity<List<ChatRoomResponse>> findChatRooms(@CurrentUser User user) {
    return ResponseEntity.ok(chatRoomService.findChatRooms(user));
  }

  //채팅방 들어가기
  //TODO : 호세님 코드 가져오기

  @DeleteMapping("/{chatRoomId}")
  public ResponseEntity<Void> deleteChatRoom(@CurrentUser User user, @PathVariable Long chatRoomId) {
    chatRoomService.deleteChatRoom(user);
    return ResponseEntity.ok().build();
  }
}
