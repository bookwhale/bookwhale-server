package com.bookwhale.chatroom.controller;

import com.bookwhale.chatroom.dto.ChatRoomCreateRequest;
import com.bookwhale.chatroom.dto.ChatRoomResponse;
import com.bookwhale.chatroom.service.ChatRoomService;
import com.bookwhale.security.CurrentUser;
import com.bookwhale.user.domain.User;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/room")
@RequiredArgsConstructor
public class ChatRoomController {

  private final ChatRoomService chatRoomService;

  @PostMapping
  public ResponseEntity<Void> createChatRoom(@CurrentUser User user,
      @Valid @RequestBody ChatRoomCreateRequest request) throws URISyntaxException {
    chatRoomService.createChatRoom(user, request);
    return ResponseEntity.created(new URI("/api/room")).build();
  }

  @GetMapping
  public ResponseEntity<List<ChatRoomResponse>> findChatRooms(@CurrentUser User user) {
    return ResponseEntity.ok(chatRoomService.findChatRooms(user));
  }

  @DeleteMapping("/{roomId}")
  public ResponseEntity<Void> deleteChatRoom(@CurrentUser User user, @PathVariable Long roomId) {
    chatRoomService.deleteChatRoom(user, roomId);
    return ResponseEntity.ok().build();
  }
}
