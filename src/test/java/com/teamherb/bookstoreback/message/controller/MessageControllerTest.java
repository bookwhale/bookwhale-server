package com.teamherb.bookstoreback.message.controller;

import static java.util.List.of;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.teamherb.bookstoreback.common.Pagination;
import com.teamherb.bookstoreback.common.controller.CommonApiTest;
import com.teamherb.bookstoreback.common.security.WithMockCustomUser;
import com.teamherb.bookstoreback.message.docs.MessageDocumentation;
import com.teamherb.bookstoreback.message.dto.MessageResponse;
import com.teamherb.bookstoreback.message.service.MessageService;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

@DisplayName("메세지 단위 테스트(Controller)")
@WebMvcTest(controllers = MessageController.class)
public class MessageControllerTest extends CommonApiTest {

  @MockBean
  MessageService messageService;

  @MockBean
  SimpMessagingTemplate messagingTemplate;

  @Test
  @DisplayName("채팅방의 이전 메세지들을 조회한다.")
  @WithMockCustomUser
  public void findMessages() throws Exception {
    Long roomId = 1L;
    Pagination pagination = new Pagination(0, 10);
    MessageResponse messageResponse = MessageResponse.builder()
        .senderId(1L)
        .senderIdentity("highright96")
        .content("안녕하세요.")
        .createdDate(LocalDateTime.now())
        .build();

    when(messageService.findMessages(any(), any())).thenReturn(of(messageResponse));

    mockMvc.perform(RestDocumentationRequestBuilders.get(
                "/api/chat/room/{roomId}/messages?page={page}&size={size}", roomId,
                pagination.getPage(), pagination.getSize())
            .header(HttpHeaders.AUTHORIZATION, "accessToken"))
        .andExpect(status().isOk())
        .andDo(print())
        .andDo(MessageDocumentation.findMessages());
  }

  @Test
  @DisplayName("채팅방의 마지막 메세지를 조회한다.")
  @WithMockCustomUser
  public void findLastMessage() throws Exception {
    Long roomId = 1L;
    MessageResponse messageResponse = MessageResponse.builder()
        .senderId(1L)
        .senderIdentity("highright96")
        .content("안녕하세요.")
        .createdDate(LocalDateTime.now())
        .build();

    when(messageService.findLastMessage(any())).thenReturn(messageResponse);

    mockMvc.perform(
            RestDocumentationRequestBuilders.get("/api/chat/room/{roomId}/last-message", roomId)
                .header(HttpHeaders.AUTHORIZATION, "accessToken"))
        .andExpect(status().isOk())
        .andDo(print())
        .andDo(MessageDocumentation.findLastMessage());
  }
}
