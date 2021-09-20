package com.teamherb.bookstoreback.message.controller;

import com.teamherb.bookstoreback.common.Pagination;
import com.teamherb.bookstoreback.message.dto.MessageResponse;
import com.teamherb.bookstoreback.message.service.MessageService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MessageController {

  private final MessageService messageService;

  @GetMapping("/api/chat/room/{roomId}/messages")
  public ResponseEntity<List<MessageResponse>> findMessages(@PathVariable Long roomId,
      @RequestParam Pagination pagination) {
    return ResponseEntity.ok(messageService.findMessages(roomId, pagination));
  }

  @GetMapping("/api/chat/room/{roomId}/last-message")
  public ResponseEntity<MessageResponse> findLastMessage(@PathVariable Long roomId) {
    return ResponseEntity.ok(messageService.findLastMessage(roomId));
  }
}
