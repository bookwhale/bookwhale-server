package com.teamherb.bookstoreback.message.acceptance;

import static com.teamherb.bookstoreback.config.StompWebSocketConfig.PUB_PREFIX;
import static com.teamherb.bookstoreback.config.StompWebSocketConfig.WS_ENDPOINT;
import static com.teamherb.bookstoreback.message.controller.MessageController.PUBLISH;
import static com.teamherb.bookstoreback.message.controller.MessageController.SUBSCRIBE;
import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamherb.bookstoreback.message.dto.MessageRequest;
import com.teamherb.bookstoreback.message.dto.MessageResponse;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.messaging.converter.ByteArrayMessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

@DisplayName("STOMP + Web Socket 통합 테스트")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class WebSocketStompTest {

  @LocalServerPort
  private int port;

  @Autowired
  private ObjectMapper objectMapper;

  private StompSession session;

  private BlockingQueue<String> blockingQueue;

  @BeforeEach
  void setUp() throws Exception {
    WebSocketStompClient stompClient = new WebSocketStompClient(
        new SockJsClient(List.of(new WebSocketTransport(new StandardWebSocketClient()))));

    // STOMP + Web Socket 연결
    session = stompClient.connect(format("ws://localhost:%d" + WS_ENDPOINT, port),
        new StompSessionHandlerAdapter() {
        }).get(1, SECONDS);

    stompClient.setMessageConverter(new ByteArrayMessageConverter());
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