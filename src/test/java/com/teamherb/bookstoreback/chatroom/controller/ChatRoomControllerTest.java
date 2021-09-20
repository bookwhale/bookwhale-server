package com.teamherb.bookstoreback.chatroom.controller;

import com.teamherb.bookstoreback.chatroom.service.ChatRoomService;
import com.teamherb.bookstoreback.common.controller.CommonApiTest;
import com.teamherb.bookstoreback.post.controller.PostController;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@DisplayName("채팅방 단위 테스트(Controller)")
@WebMvcTest(controllers = PostController.class)
public class ChatRoomControllerTest extends CommonApiTest {

  @MockBean
  ChatRoomService chatRoomService;


}
