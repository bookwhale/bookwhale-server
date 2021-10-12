package com.teamherb.bookstoreback.message.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.teamherb.bookstoreback.common.TestConfig;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;

@DisplayName("메세지 단위 테스트(Repository)")
@Import(TestConfig.class)
@DataJpaTest
public class MessageRepositoryTest {

  @Autowired
  private MessageRepository messageRepository;

  Long roomId = 1L;

  List<Message> messages;

  @BeforeEach
  void setUp() {
    messages = List.of(Message.builder()
            .roomId(roomId)
            .content("1번 메세지입니다.")
            .createdDate(LocalDateTime.of(2021, 10, 18, 1, 1))
            .build(),
        Message.builder()
            .roomId(roomId)
            .content("2번 메세지입니다.")
            .createdDate(LocalDateTime.of(2021, 10, 18, 1, 2))
            .build(),
        Message.builder()
            .roomId(roomId)
            .content("3번 메세지입니다.")
            .createdDate(LocalDateTime.of(2021, 10, 18, 1, 3))
            .build(),
        Message.builder()
            .roomId(roomId)
            .content("4번 메세지입니다.")
            .createdDate(LocalDateTime.of(2021, 10, 18, 1, 4))
            .build(),
        Message.builder()
            .roomId(roomId)
            .content("5번 메세지입니다.")
            .createdDate(LocalDateTime.of(2021, 10, 18, 1, 5))
            .build(),
        Message.builder()
            .roomId(roomId)
            .content("6번 메세지입니다.")
            .createdDate(LocalDateTime.of(2021, 10, 18, 1, 6))
            .build()
    );
    messageRepository.saveAll(messages);
  }

  @DisplayName("채팅방의 모든 메세지를 생성 시간을 기준 내림차순으로 조회한다.")
  @Test
  void findAllByRoomIdOrderByCreatedDateDesc() {
    PageRequest pageRequest = PageRequest.of(0, 5);

    List<Message> result = messageRepository.findAllByRoomIdOrderByCreatedDateDesc(roomId,
        pageRequest).getContent();

    Assertions.assertAll(
        () -> assertThat(result.size()).isEqualTo(pageRequest.getPageSize()),
        () -> assertThat(result.get(0).getContent()).isEqualTo(messages.get(5).getContent()),
        () -> assertThat(result.get(1).getContent()).isEqualTo(messages.get(4).getContent()),
        () -> assertThat(result.get(2).getContent()).isEqualTo(messages.get(3).getContent()),
        () -> assertThat(result.get(3).getContent()).isEqualTo(messages.get(2).getContent()),
        () -> assertThat(result.get(4).getContent()).isEqualTo(messages.get(1).getContent())
    );
  }

  @DisplayName("채팅방의 마지막 메세지를 조회한다.")
  @Test
  void findTopByRoomIdOrderByCreatedDateDesc() {
    Message result = messageRepository.findTopByRoomIdOrderByCreatedDateDesc(roomId).get();
    assertThat(result.getContent()).isEqualTo(messages.get(5).getContent());
  }
}