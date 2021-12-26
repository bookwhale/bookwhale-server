package com.bookwhale.chatroom.acceptance;

import com.bookwhale.article.dto.ArticleRequest;
import com.bookwhale.article.dto.BookRequest;
import com.bookwhale.chatroom.acceptance.step.ChatRoomAcceptanceStep;
import com.bookwhale.chatroom.dto.ChatRoomResponse;
import com.bookwhale.common.acceptance.AcceptanceTest;
import com.bookwhale.common.acceptance.step.AcceptanceStep;
import com.bookwhale.user.acceptance.step.UserAcceptanceStep;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("채팅방 통합 테스트")
public class ChatRoomAcceptanceTest extends AcceptanceTest {

    ArticleRequest articleRequest;

    @BeforeEach
    @Override
    public void setUp() {
        super.setUp();
        articleRequest = ArticleRequest.builder()
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
            .sellingLocation("INCHEON")
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
            sellerJwt, articleRequest);

        AcceptanceStep.assertThatStatusIsCreated(res);
    }

    @DisplayName("채팅방들을 조회한다.")
    @Test
    void findChatRooms() {
        String loginUserJwt = UserAcceptanceStep.requestToLoginAndGetAccessToken(loginRequest);
        String sellerJwt = UserAcceptanceStep.requestToLoginAndGetAccessToken(anotherLoginRequest);

        ChatRoomAcceptanceStep.createChatRoom(loginUserJwt, sellerJwt, articleRequest);

        ExtractableResponse<Response> loginUserRes = ChatRoomAcceptanceStep.requestToFindChatRooms(
            loginUserJwt);
        ExtractableResponse<Response> anotherUserRes = ChatRoomAcceptanceStep.requestToFindChatRooms(
            sellerJwt);

        List<ChatRoomResponse> loginUserRoomRes = loginUserRes.jsonPath()
            .getList("", ChatRoomResponse.class);
        List<ChatRoomResponse> anotherRoomRes = anotherUserRes.jsonPath()
            .getList("", ChatRoomResponse.class);

        AcceptanceStep.assertThatStatusIsOk(loginUserRes);
        AcceptanceStep.assertThatStatusIsOk(anotherUserRes);

        ChatRoomAcceptanceStep.assertThatFindChatRooms(loginUserRoomRes, articleRequest,
            anotherUser);
        ChatRoomAcceptanceStep.assertThatFindChatRooms(anotherRoomRes, articleRequest, user);
    }

    @DisplayName("상대방이 나간 채팅방은 isOpponentDelete 가 false 로 조회된다.")
    @Test
    void findChatRooms_opponentDelete() {
        String loginUserJwt = UserAcceptanceStep.requestToLoginAndGetAccessToken(loginRequest);
        String sellerJwt = UserAcceptanceStep.requestToLoginAndGetAccessToken(anotherLoginRequest);

        ChatRoomAcceptanceStep.createChatRoom(loginUserJwt, sellerJwt, articleRequest);

        //상대방이 채팅방을 나간다
        ExtractableResponse<Response> anotherUserRes = ChatRoomAcceptanceStep.requestToFindChatRooms(
            sellerJwt);
        List<ChatRoomResponse> anotherRoomRes = anotherUserRes.jsonPath()
            .getList("", ChatRoomResponse.class);
        ChatRoomAcceptanceStep.requestToDeleteChatRoom(sellerJwt,
            anotherRoomRes.get(0).getRoomId());

        //상대방이 나간 채팅방은 isOpponentDelete 가 false 로 조회된다.
        ExtractableResponse<Response> loginUserRes = ChatRoomAcceptanceStep.requestToFindChatRooms(
            loginUserJwt);
        List<ChatRoomResponse> loginUserRoomRes = loginUserRes.jsonPath()
            .getList("", ChatRoomResponse.class);
        AcceptanceStep.assertThatStatusIsOk(loginUserRes);
        Assertions.assertThat(loginUserRoomRes.get(0).isOpponentDelete()).isEqualTo(true);
    }

    @DisplayName("나간 채팅방은 조회되지 않는다.")
    @Test
    void findChatRooms_deleteRoom() {
        String loginUserJwt = UserAcceptanceStep.requestToLoginAndGetAccessToken(loginRequest);
        String sellerJwt = UserAcceptanceStep.requestToLoginAndGetAccessToken(anotherLoginRequest);

        ChatRoomAcceptanceStep.createChatRoom(loginUserJwt, sellerJwt, articleRequest);

        //채팅방을 나간다
        List<ChatRoomResponse> loginUserRoomRes = ChatRoomAcceptanceStep.requestToFindChatRooms(
            loginUserJwt).jsonPath().getList("", ChatRoomResponse.class);
        ChatRoomAcceptanceStep.requestToDeleteChatRoom(loginUserJwt,
            loginUserRoomRes.get(0).getRoomId());

        //나간 채팅방은 조회되지 않는다.
        ExtractableResponse<Response> res = ChatRoomAcceptanceStep.requestToFindChatRooms(
            loginUserJwt);
        List<ChatRoomResponse> roomRes = res.jsonPath().getList("", ChatRoomResponse.class);
        AcceptanceStep.assertThatStatusIsOk(res);
        Assertions.assertThat(roomRes.size()).isEqualTo(0);
    }

    @DisplayName("채팅방을 삭제한 후 조회한다.")
    @Test
    void deleteChatRoom() {
        String loginUserJwt = UserAcceptanceStep.requestToLoginAndGetAccessToken(loginRequest);
        String sellerJwt = UserAcceptanceStep.requestToLoginAndGetAccessToken(anotherLoginRequest);

        ChatRoomAcceptanceStep.createChatRoom(loginUserJwt, sellerJwt, articleRequest);
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
