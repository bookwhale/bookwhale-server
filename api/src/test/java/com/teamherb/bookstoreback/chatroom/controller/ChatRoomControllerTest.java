package com.teamherb.bookstoreback.chatroom.controller;

import static java.util.List.of;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.teamherb.bookstoreback.chatroom.docs.ChatRoomDocumentations;
import com.teamherb.bookstoreback.chatroom.dto.ChatRoomCreateRequest;
import com.teamherb.bookstoreback.chatroom.dto.ChatRoomResponse;
import com.teamherb.bookstoreback.chatroom.service.ChatRoomService;
import com.teamherb.bookstoreback.common.controller.CommonApiTest;
import com.teamherb.bookstoreback.common.security.WithMockCustomUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

@DisplayName("채팅방 단위 테스트(Controller)")
@WebMvcTest(controllers = ChatRoomController.class)
public class ChatRoomControllerTest extends CommonApiTest {

  @MockBean
  ChatRoomService chatRoomService;

  @WithMockCustomUser
  @DisplayName("거래 요청 메일을 보낸 후 채팅방을 생성한다.")
  @Test
  void createChatRoom() throws Exception {
    ChatRoomCreateRequest request = ChatRoomCreateRequest.builder()
        .postId(1L)
        .sellerId(1L)
        .build();

    doNothing().when(chatRoomService).createChatRoom(any(), any());

    mockMvc.perform(post("/api/room")
            .header(HttpHeaders.AUTHORIZATION, "accessToken")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(header().string("location", "/api/room"))
        .andExpect(status().isCreated())
        .andDo(print())
        .andDo(ChatRoomDocumentations.createChatRoom());
  }

  @WithMockCustomUser
  @DisplayName("채팅방들을 조회한다.")
  @Test
  void findChatRooms() throws Exception {
    ChatRoomResponse response = ChatRoomResponse.builder()
        .roomId(1L)
        .postId(1L)
        .postTitle("토비의 스프링 팝니다.")
        .postImage("이미지")
        .opponentIdentity("highright96")
        .opponentProfile("profile")
        .isOpponentDelete(false)
        .build();

    when(chatRoomService.findChatRooms(any())).thenReturn(of(response));

    mockMvc.perform(get("/api/room")
            .header(HttpHeaders.AUTHORIZATION, "accessToken"))
        .andExpect(status().isOk())
        .andDo(print())
        .andDo(ChatRoomDocumentations.findChatRooms());
  }

  @WithMockCustomUser
  @DisplayName("채팅방을 삭제한다.")
  @Test
  void deleteChatRoom() throws Exception {
    Long roomId = 1L;

    doNothing().when(chatRoomService).deleteChatRoom(any(), any());

    mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/room/{roomId}", roomId)
            .header(HttpHeaders.AUTHORIZATION, "accessToken"))
        .andExpect(status().isOk())
        .andDo(print())
        .andDo(ChatRoomDocumentations.deleteChatRoom());
  }
}
