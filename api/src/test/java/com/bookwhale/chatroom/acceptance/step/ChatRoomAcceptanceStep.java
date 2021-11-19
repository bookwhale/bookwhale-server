package com.bookwhale.chatroom.acceptance.step;

import static com.bookwhale.post.acceptance.step.PostAcceptanceStep.requestToCreatePost;
import static com.bookwhale.post.acceptance.step.PostAcceptanceStep.requestToFindPost;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.bookwhale.chatroom.dto.ChatRoomCreateRequest;
import com.bookwhale.chatroom.dto.ChatRoomResponse;
import com.bookwhale.post.dto.PostRequest;
import com.bookwhale.post.dto.PostResponse;
import com.bookwhale.user.domain.User;
import com.bookwhale.common.acceptance.AcceptanceUtils;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public class ChatRoomAcceptanceStep {

    public static void assertThatFindChatRooms(List<ChatRoomResponse> responses,
        PostRequest request, User opponent) {
        Assertions.assertAll(
            () -> assertThat(responses.size()).isEqualTo(1),
            () -> assertThat(responses.get(0).getPostTitle()).isEqualTo(request.getTitle()),
            () -> assertThat(responses.get(0).getPostImage()).isNotNull(),
            () -> assertThat(responses.get(0).getOpponentIdentity()).isEqualTo(
                opponent.getIdentity()),
            () -> assertThat(responses.get(0).getOpponentProfile()).isEqualTo(
                opponent.getProfileImage())
        );
    }

    public static void assertThatDeleteChatRoom(List<ChatRoomResponse> loginUserRoomRes,
        List<ChatRoomResponse> sellerRoomRes) {
        Assertions.assertAll(
            () -> assertThat(loginUserRoomRes.size()).isEqualTo(0),
            () -> assertThat(sellerRoomRes.size()).isEqualTo(1),
            () -> assertThat(sellerRoomRes.get(0).isOpponentDelete()).isEqualTo(true)
        );
    }

    public static ExtractableResponse<Response> requestToCreateChatRoom(String jwt,
        ChatRoomCreateRequest request) {
        return given().log().all()
            .header(HttpHeaders.AUTHORIZATION, jwt)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(request)
            .when()
            .post("/api/room")
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> requestToFindChatRooms(String jwt) {
        return given().log().all()
            .header(HttpHeaders.AUTHORIZATION, jwt)
            .when()
            .get("/api/room")
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> requestToDeleteChatRoom(String jwt, Long roomId) {
        return given().log().all()
            .header(HttpHeaders.AUTHORIZATION, jwt)
            .when()
            .delete("/api/room/{roomId}", roomId)
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> createChatRoom(String loginUserJwt,
        String sellerJwt,
        PostRequest postRequest) {
        Long postId = AcceptanceUtils.getIdFromResponse(
            requestToCreatePost(sellerJwt, postRequest));
        PostResponse postResponse = requestToFindPost(loginUserJwt, postId).jsonPath()
            .getObject("", PostResponse.class);

        ChatRoomCreateRequest request = ChatRoomCreateRequest.builder()
            .postId(postResponse.getPostId())
            .sellerId(postResponse.getSellerId())
            .build();

        return ChatRoomAcceptanceStep.requestToCreateChatRoom(loginUserJwt, request);
    }
}
