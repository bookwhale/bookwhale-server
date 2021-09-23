package com.teamherb.bookstoreback.message.acceptance.step;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.teamherb.bookstoreback.common.Pagination;
import com.teamherb.bookstoreback.message.domain.Message;
import com.teamherb.bookstoreback.message.dto.MessageResponse;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpHeaders;

public class MessageAcceptanceStep {

  public static void assertThatFindMessages(List<MessageResponse> messageResponses,
      List<Message> messages, Pagination pagination) {
    Assertions.assertAll(
        () -> assertThat(messageResponses.size()).isEqualTo(pagination.getSize()),
        () -> assertThat(messageResponses.get(0).getContent()).isEqualTo(
            messages.get(5).getContent()),
        () -> assertThat(messageResponses.get(1).getContent()).isEqualTo(
            messages.get(4).getContent()),
        () -> assertThat(messageResponses.get(2).getContent()).isEqualTo(
            messages.get(3).getContent()),
        () -> assertThat(messageResponses.get(3).getContent()).isEqualTo(
            messages.get(2).getContent()),
        () -> assertThat(messageResponses.get(4).getContent()).isEqualTo(
            messages.get(1).getContent())
    );
  }

  public static ExtractableResponse<Response> requestToFindMessages(String jwt, Long roomId,
      Pagination pagination) {
    return given().log().all()
        .header(HttpHeaders.AUTHORIZATION, jwt)
        .when()
        .get(String.format("/api/chat/room/%d/messages?page=%d&size=%d", roomId,
            pagination.getPage(), pagination.getSize()))
        .then().log().all()
        .extract();
  }

  public static ExtractableResponse<Response> requestToFindLastMessage(String jwt, Long roomId) {
    return given().log().all()
        .header(HttpHeaders.AUTHORIZATION, jwt)
        .when()
        .get(String.format("/api/chat/room/%d/last-message", roomId))
        .then().log().all()
        .extract();
  }
}
