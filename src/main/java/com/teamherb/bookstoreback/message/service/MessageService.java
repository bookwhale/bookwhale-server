package com.teamherb.bookstoreback.message.service;

import com.teamherb.bookstoreback.common.Pagination;
import com.teamherb.bookstoreback.message.domain.Message;
import com.teamherb.bookstoreback.message.domain.MessageRepository;
import com.teamherb.bookstoreback.message.dto.MessageResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MessageService {

  private final MessageRepository messageRepository;

  @Transactional(readOnly = true)
  public List<MessageResponse> findMessages(Long roomId, Pagination pagination) {
    PageRequest pageable = PageRequest.of(pagination.getPage(), pagination.getSize());
    return MessageResponse.listOf(
        messageRepository.findAllByRoomIdOrderByCreatedDateDesc(roomId, pageable).getContent());
  }

  @Transactional(readOnly = true)
  public MessageResponse findLastMessage(Long roomId) {
    Message message = messageRepository.findTopByRoomIdOrderByCreatedDateDesc(roomId)
        .orElseGet(Message::createEmptyMessage);
    return MessageResponse.of(message);
  }
}
