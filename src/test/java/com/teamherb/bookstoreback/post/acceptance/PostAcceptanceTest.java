package com.teamherb.bookstoreback.post.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import com.teamherb.bookstoreback.common.acceptance.AcceptanceTest;
import com.teamherb.bookstoreback.common.acceptance.step.AcceptanceStep;
import com.teamherb.bookstoreback.post.acceptance.step.PostAcceptanceStep;
import com.teamherb.bookstoreback.post.dto.BookResponse;
import com.teamherb.bookstoreback.post.dto.NaverBookRequest;
import com.teamherb.bookstoreback.user.acceptance.step.UserAcceptanceStep;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class PostAcceptanceTest extends AcceptanceTest {

    @DisplayName("ISBN 으로 책을 검색한다.")
    @Test
    void findNaverBooks_isbn() {
        NaverBookRequest naverBookRequest = NaverBookRequest.builder()
            .isbn("8960773433")
            .build();

        String jwt = UserAcceptanceStep.requestToLoginAndGetAccessToken(loginRequest);

        ExtractableResponse<Response> response = PostAcceptanceStep.requestToFindNaverBooks(
            jwt, naverBookRequest);
        BookResponse bookResponse = response.jsonPath().getList(".", BookResponse.class).get(0);

        AcceptanceStep.assertThatStatusIsOk(response);
        PostAcceptanceStep.assertThatFindNaverBooks(bookResponse);
    }

    @DisplayName("제목으로 책을 검색한다.")
    @Test
    void findNaverBooks_title() {
        NaverBookRequest naverBookRequest = NaverBookRequest.builder()
            .title("토비")
            .build();

        String jwt = UserAcceptanceStep.requestToLoginAndGetAccessToken(loginRequest);

        ExtractableResponse<Response> response = PostAcceptanceStep.requestToFindNaverBooks(
            jwt, naverBookRequest);
        List<BookResponse> bookResponses = response.jsonPath().getList(".", BookResponse.class);

        AcceptanceStep.assertThatStatusIsOk(response);
        assertThat(bookResponses.size()).isGreaterThan(1);
    }
}
