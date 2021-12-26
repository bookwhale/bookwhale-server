package com.bookwhale.chatroom.acceptance.step;

import static com.bookwhale.article.acceptance.step.ArticleAcceptanceStep.requestToCreateArticle;
import static com.bookwhale.article.acceptance.step.ArticleAcceptanceStep.requestToFindArticle;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.bookwhale.article.dto.ArticleRequest;
import com.bookwhale.article.dto.ArticleResponse;
import com.bookwhale.chatroom.dto.ChatRoomCreateRequest;
import com.bookwhale.chatroom.dto.ChatRoomResponse;
import com.bookwhale.common.acceptance.AcceptanceUtils;
import com.bookwhale.user.domain.User;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public class ChatRoomAcceptanceStep {

    public static void assertThatFindChatRooms(List<ChatRoomResponse> responses,
        ArticleRequest request, User opponent) {
        Assertions.assertAll(
            () -> assertThat(responses.size()).isEqualTo(1),
            () -> assertThat(responses.get(0).getArticleTitle()).isEqualTo(request.getTitle()),
            () -> assertThat(responses.get(0).getArticleImage()).isNotNull(),
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
        ArticleRequest articleRequest) {
        Long articleId = AcceptanceUtils.getIdFromResponse(
            requestToCreateArticle(sellerJwt, articleRequest));
        ArticleResponse articleResponse = requestToFindArticle(loginUserJwt, articleId).jsonPath()
            .getObject("", ArticleResponse.class);

        ChatRoomCreateRequest request = ChatRoomCreateRequest.builder()
            .articleId(articleResponse.getArticleId())
            .sellerId(articleResponse.getSellerId())
            .build();

        return ChatRoomAcceptanceStep.requestToCreateChatRoom(loginUserJwt, request);
    }
}
