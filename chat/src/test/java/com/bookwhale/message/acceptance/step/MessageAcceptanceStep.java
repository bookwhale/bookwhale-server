package com.bookwhale.message.acceptance.step;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.bookwhale.common.dto.Pagination;
import com.bookwhale.message.dto.MessageResponse;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.junit.jupiter.api.Assertions;

public class MessageAcceptanceStep {

    public static void assertThatFindMessages(List<MessageResponse> messageResponses) {
        Assertions.assertAll(
            () -> assertThat(messageResponses.size()).isEqualTo(2),
            () -> assertThat(messageResponses.get(0)).isNotNull(),
            () -> assertThat(messageResponses.get(1)).isNotNull()
        );
    }

    public static ExtractableResponse<Response> requestToFindMessages(Long roomId,
        Pagination pagination) {
        return given().log().all()
            .when()
            .get(String.format("/api/message/%d?page=%d&size=%d", roomId,
                pagination.getPage(), pagination.getSize()))
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> requestToFindLastMessage(Long roomId) {
        return given().log().all()
            .when()
            .get(String.format("/api/message/%d/last", roomId))
            .then().log().all()
            .extract();
    }
}
