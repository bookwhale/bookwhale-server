package com.bookwhale.chatroom.controller;

import static java.util.List.of;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bookwhale.chatroom.docs.ChatRoomDocumentations;
import com.bookwhale.chatroom.dto.ChatRoomCreateRequest;
import com.bookwhale.chatroom.dto.ChatRoomResponse;
import com.bookwhale.chatroom.service.ChatRoomService;
import com.bookwhale.common.controller.CommonApiTest;
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

    @DisplayName("거래 요청 메일을 보낸 후 채팅방을 생성한다.")
    @Test
    void createChatRoom() throws Exception {
        ChatRoomCreateRequest request = ChatRoomCreateRequest.builder()
            .articleId(1L)
            .sellerId(1L)
            .build();

        doNothing().when(chatRoomService).createChatRoom(any(), any());

        mockMvc.perform(post("/api/room")
                .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(header().string("location", "/api/room"))
            .andExpect(status().isCreated())
            .andDo(print())
            .andDo(ChatRoomDocumentations.createChatRoom());
    }


    @DisplayName("채팅방들을 조회한다.")
    @Test
    void findChatRooms() throws Exception {
        ChatRoomResponse response = ChatRoomResponse.builder()
            .roomId(1L)
            .articleId(1L)
            .articleTitle("토비의 스프링 팝니다.")
            .articleImage("이미지")
            .opponentIdentity("highright96")
            .opponentProfile("profile")
            .isOpponentDelete(false)
            .lastContent("안녕하세요.")
            .build();

        when(chatRoomService.findChatRooms(any())).thenReturn(of(response));

        mockMvc.perform(get("/api/room")
                .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken"))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(ChatRoomDocumentations.findChatRooms());
    }


    @DisplayName("채팅방을 삭제한다.")
    @Test
    void deleteChatRoom() throws Exception {
        Long roomId = 1L;

        doNothing().when(chatRoomService).deleteChatRoom(any(), any());

        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/room/{roomId}", roomId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken"))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(ChatRoomDocumentations.deleteChatRoom());
    }
}
