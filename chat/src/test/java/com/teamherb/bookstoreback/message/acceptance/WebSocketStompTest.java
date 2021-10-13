package com.teamherb.bookstoreback.message.acceptance;

import static com.teamherb.bookstoreback.config.StompWebSocketConfig.PUB_PREFIX;
import static com.teamherb.bookstoreback.message.controller.MessageController.PUBLISH;
import static com.teamherb.bookstoreback.message.controller.MessageController.SUBSCRIBE;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.teamherb.bookstoreback.message.dto.MessageRequest;
import com.teamherb.bookstoreback.message.dto.MessageResponse;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.LinkedBlockingDeque;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

@DisplayName("STOMP + Web Socket 통합 테스트")
public class WebSocketStompTest extends AcceptanceTest {

  @BeforeEach
  @Override
  public void setUp() throws Exception {
    super.setUp();
  }

  @DisplayName("채팅방에 메세지를 보내면 채팅방을 구독한 유저는 메세지를 받는다.")
  @Test
  void verifyMessageIsReceived() throws JsonProcessingException, InterruptedException {
    Long roomId = 1L;
    MessageRequest request = new MessageRequest(roomId, 1L, "남상우", "안녕하세요");
    blockingQueue = new LinkedBlockingDeque<>();

    session.subscribe(SUBSCRIBE + roomId, new defaultSessionHandlerAdapter());

    session.send(PUB_PREFIX + PUBLISH,
        objectMapper.writeValueAsString(request).getBytes(StandardCharsets.UTF_8));

    MessageResponse response = objectMapper.readValue(blockingQueue.poll(1, SECONDS),
        MessageResponse.class);

    assertAll(
        () -> assertThat(response.getContent()).isEqualTo(request.getContent()),
        () -> assertThat(response.getSenderIdentity()).isEqualTo(request.getSenderIdentity()),
        () -> assertThat(response.getSenderId()).isEqualTo(request.getSenderId())
    );
  }


  public class defaultSessionHandlerAdapter extends StompSessionHandlerAdapter {

    @Override
    public Type getPayloadType(StompHeaders headers) {
      return byte[].class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
      blockingQueue.add(new String((byte[]) payload));
    }
  }
}
