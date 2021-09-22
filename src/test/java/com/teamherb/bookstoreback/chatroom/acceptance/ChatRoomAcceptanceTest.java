package com.teamherb.bookstoreback.chatroom.acceptance;

import com.teamherb.bookstoreback.chatroom.acceptance.step.ChatRoomAcceptanceStep;
import com.teamherb.bookstoreback.chatroom.dto.ChatRoomResponse;
import com.teamherb.bookstoreback.common.acceptance.AcceptanceTest;
import com.teamherb.bookstoreback.common.acceptance.step.AcceptanceStep;
import com.teamherb.bookstoreback.post.dto.BookRequest;
import com.teamherb.bookstoreback.post.dto.PostRequest;
import com.teamherb.bookstoreback.user.acceptance.step.UserAcceptanceStep;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ChatRoomAcceptanceTest extends AcceptanceTest {

  PostRequest postRequest;

  @BeforeEach
  @Override
  public void setUp() {
    super.setUp();
    postRequest = PostRequest.builder()
        .bookRequest(BookRequest.builder()
            .bookSummary("책 설명")
            .bookPubDate("2021-12-12")
            .bookIsbn("12345678910")
            .bookListPrice("10000")
            .bookThumbnail("썸네일")
            .bookTitle("토비의 스프링")
            .bookPublisher("허브출판사")
            .bookAuthor("이일민")
            .build())
        .title("토비의 스프링 팝니다~")
        .description("책 설명")
        .bookStatus("BEST")
        .price("5000")
        .build();
  }

  /*
  user : 구매자, 로그인 유저
  anotherUser : 판매자, 상대방
  */
  @DisplayName("채팅방을 생성한다.")
  @Test
  void createChatRoom() {
    String loginUserJwt = UserAcceptanceStep.requestToLoginAndGetAccessToken(loginRequest);
    String sellerJwt = UserAcceptanceStep.requestToLoginAndGetAccessToken(anotherLoginRequest);

    ExtractableResponse<Response> res = ChatRoomAcceptanceStep.createChatRoom(loginUserJwt,
        sellerJwt, postRequest);

    AcceptanceStep.assertThatStatusIsCreated(res);
  }

  @DisplayName("채팅방들을 조회한다.")
  @Test
  void findChatRooms() {
    String loginUserJwt = UserAcceptanceStep.requestToLoginAndGetAccessToken(loginRequest);
    String sellerJwt = UserAcceptanceStep.requestToLoginAndGetAccessToken(anotherLoginRequest);

    ChatRoomAcceptanceStep.createChatRoom(loginUserJwt, sellerJwt, postRequest);

    ExtractableResponse<Response> res = ChatRoomAcceptanceStep.requestToFindChatRooms(loginUserJwt);
    List<ChatRoomResponse> roomResponses = res.jsonPath().getList("", ChatRoomResponse.class);

    AcceptanceStep.assertThatStatusIsOk(res);
    ChatRoomAcceptanceStep.assertThatFindChatRooms(roomResponses, postRequest, anotherUser);
  }

  @DisplayName("채팅방을 삭제한 후 조회한다.")
  @Test
  void deleteChatRoom() {
    String loginUserJwt = UserAcceptanceStep.requestToLoginAndGetAccessToken(loginRequest);
    String sellerJwt = UserAcceptanceStep.requestToLoginAndGetAccessToken(anotherLoginRequest);

    ChatRoomAcceptanceStep.createChatRoom(loginUserJwt, sellerJwt, postRequest);
    Long roomId = ChatRoomAcceptanceStep.requestToFindChatRooms(loginUserJwt).jsonPath()
        .getList("", ChatRoomResponse.class).get(0).getRoomId();

    ExtractableResponse<Response> res = ChatRoomAcceptanceStep.requestToDeleteChatRoom(
        loginUserJwt, roomId);

    List<ChatRoomResponse> loginUserRoomRes = ChatRoomAcceptanceStep.requestToFindChatRooms(
        loginUserJwt).jsonPath().getList("", ChatRoomResponse.class);

    List<ChatRoomResponse> sellerRoomRes = ChatRoomAcceptanceStep.requestToFindChatRooms(
        sellerJwt).jsonPath().getList("", ChatRoomResponse.class);

    AcceptanceStep.assertThatStatusIsOk(res);
    ChatRoomAcceptanceStep.assertThatDeleteChatRoom(loginUserRoomRes, sellerRoomRes);
  }
}
