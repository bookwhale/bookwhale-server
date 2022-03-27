package com.bookwhale.message.service;

import com.bookwhale.common.dto.Pagination;
import com.bookwhale.message.domain.Message;
import com.bookwhale.message.domain.MessageRepository;
import com.bookwhale.message.dto.MessageRequest;
import com.bookwhale.message.dto.MessageResponse;
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

    public MessageResponse saveMessage(MessageRequest request) {
        Message message = messageRepository.save(Message.create(request.toEntity()));
        return MessageResponse.of(message);
    }
}
