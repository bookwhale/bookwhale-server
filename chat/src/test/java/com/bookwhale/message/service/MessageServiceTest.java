package com.bookwhale.message.service;

import static java.util.List.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bookwhale.common.dto.Pagination;
import com.bookwhale.message.domain.Message;
import com.bookwhale.message.domain.MessageRepository;
import com.bookwhale.message.dto.MessageResponse;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

@ExtendWith(MockitoExtension.class)
@DisplayName("메세지 단위 테스트(Service)")
public class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    MessageService messageService;

    @BeforeEach
    void setUp() {
        messageService = new MessageService(messageRepository);
    }

    @DisplayName("채팅방의 이전 메세지들을 조회한다.")
    @Test
    void findMessages() {
        Long roomId = 1L;
        Pagination pagination = new Pagination(0, 10);
        Message message = Message.createEmptyMessage();
        Page<Message> pages = new PageImpl<>(of(message));

        when(messageRepository.findAllByRoomIdOrderByCreatedDateDesc(any(), any())).thenReturn(
            pages);

        List<MessageResponse> responses = messageService.findMessages(roomId, pagination);

        verify(messageRepository).findAllByRoomIdOrderByCreatedDateDesc(any(), any());
        assertAll(
            () -> assertThat(responses.size()).isEqualTo(1),
            () -> assertThat(responses.get(0).getContent()).isEqualTo(message.getContent()),
            () -> assertThat(responses.get(0).getSenderId()).isEqualTo(message.getSenderId()),
            () -> assertThat(responses.get(0).getCreatedDate()).isEqualTo(message.getCreatedDate()),
            () -> assertThat(responses.get(0).getSenderIdentity()).isEqualTo(
                message.getSenderIdentity())
        );
    }

    @DisplayName("채팅방의 마지막 메세지를 조회한다.")
    @Test
    void findLastMessage() {
        Long roomId = 1L;
        Message message = Message.createEmptyMessage();

        when(messageRepository.findTopByRoomIdOrderByCreatedDateDesc(any())).thenReturn(
            Optional.of(message));

        MessageResponse response = messageService.findLastMessage(roomId);

        verify(messageRepository).findTopByRoomIdOrderByCreatedDateDesc(any());
        assertAll(
            () -> assertThat(response.getContent()).isEqualTo(message.getContent()),
            () -> assertThat(response.getSenderId()).isEqualTo(message.getSenderId()),
            () -> assertThat(response.getCreatedDate()).isEqualTo(message.getCreatedDate()),
            () -> assertThat(response.getSenderIdentity()).isEqualTo(
                message.getSenderIdentity())
        );
    }
}
