package com.teamherb.bookstoreback.post.acceptance.step;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.teamherb.bookstoreback.post.dto.BookResponse;
import com.teamherb.bookstoreback.post.dto.NaverBookRequest;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.MediaType;

public class PostAcceptanceStep {

    public static void assertThatFindNaverBooks(BookResponse bookResponse) {
        Assertions.assertAll(
            () -> assertThat(bookResponse.getBookTitle()).isEqualTo(
                "토비의 스프링 3.1 세트 (스프링의 이해와 원리 + 스프링의 기술과 선택, 전2권)"),
            () -> assertThat(bookResponse.getBookAuthor()).isEqualTo("이일민")
        );
    }

    public static ExtractableResponse<Response> requestToFindNaverBooks(String jwt,
        NaverBookRequest req) {
        return given().log().all()
            .header("jwt", jwt)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(req)
            .when()
            .get("/api/post/naverBookAPI?"
                + (req.getTitle() == null ? "" : "title=" + req.getTitle())
                + (req.getIsbn() == null ? "" : "isbn=" + req.getIsbn())
                + (req.getAuthor() == null ? "" : "author=" + req.getAuthor()))
            .then().log().all()
            .extract();
    }
}
