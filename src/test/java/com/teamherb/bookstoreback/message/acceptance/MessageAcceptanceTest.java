package com.teamherb.bookstoreback.message.acceptance;

import com.teamherb.bookstoreback.common.Pagination;
import com.teamherb.bookstoreback.common.acceptance.AcceptanceTest;
import com.teamherb.bookstoreback.common.acceptance.step.AcceptanceStep;
import com.teamherb.bookstoreback.message.acceptance.step.MessageAcceptanceStep;
import com.teamherb.bookstoreback.message.domain.Message;
import com.teamherb.bookstoreback.message.domain.MessageRepository;
import com.teamherb.bookstoreback.message.dto.MessageResponse;
import com.teamherb.bookstoreback.user.acceptance.step.UserAcceptanceStep;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.time.LocalDateTime;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("메세지 통합 테스트")
public class MessageAcceptanceTest extends AcceptanceTest {

  @Autowired
  MessageRepository messageRepository;

  List<Message> messages;

  Long roomId = 1L;

  @BeforeEach
  @Override
  public void setUp() {
    super.setUp();
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

  @DisplayName("채팅방의 이전 메세지들을 조회한다.")
  @Test
  void findMessages() {
    String jwt = UserAcceptanceStep.requestToLoginAndGetAccessToken(loginRequest);
    Pagination pagination = new Pagination(0, 5);

    ExtractableResponse<Response> response = MessageAcceptanceStep.requestToFindMessages(jwt,
        roomId, pagination);
    List<MessageResponse> messageResponses = response.jsonPath().getList("", MessageResponse.class);

    AcceptanceStep.assertThatStatusIsOk(response);
    MessageAcceptanceStep.assertThatFindMessages(messageResponses, messages, pagination);
  }

  @DisplayName("채팅방의 마지막 메세지를 조회한다.")
  @Test
  void findLastMessage() {
    String jwt = UserAcceptanceStep.requestToLoginAndGetAccessToken(loginRequest);

    ExtractableResponse<Response> res = MessageAcceptanceStep.requestToFindLastMessage(jwt, roomId);
    MessageResponse messageResponse = res.jsonPath().getObject("", MessageResponse.class);

    AcceptanceStep.assertThatStatusIsOk(res);
    Assertions.assertThat(messageResponse.getContent())
        .isEqualTo(messages.get(messages.size() - 1).getContent());
  }
}
