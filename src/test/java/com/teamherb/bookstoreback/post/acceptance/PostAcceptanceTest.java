package com.teamherb.bookstoreback.post.acceptance;

import com.teamherb.bookstoreback.account.dto.AccountRequest;
import com.teamherb.bookstoreback.common.acceptance.AcceptanceTest;
import com.teamherb.bookstoreback.common.acceptance.AcceptanceUtils;
import com.teamherb.bookstoreback.common.acceptance.step.AcceptanceStep;
import com.teamherb.bookstoreback.post.acceptance.step.PostAcceptanceStep;
import com.teamherb.bookstoreback.post.dto.BookRequest;
import com.teamherb.bookstoreback.post.dto.PostRequest;
import com.teamherb.bookstoreback.post.dto.PostResponse;
import com.teamherb.bookstoreback.user.acceptance.step.UserAcceptanceStep;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("게시글 통합 테스트")
public class PostAcceptanceTest extends AcceptanceTest {

    PostRequest postRequest;

    @BeforeEach
    @Override
    public void setUp() {
        super.setUp();
        AccountRequest accountRequest = AccountRequest.builder()
            .accountBank("Hankook Bank")
            .accountOwner("Nam Sang woo")
            .accountNumber("123-1234-12345")
            .build();

        BookRequest bookRequest = BookRequest.builder()
            .bookSummary("book summary")
            .bookPubDate("2021-12-12")
            .bookIsbn("12345678910")
            .bookListPrice("10000")
            .bookThumbnail("thumbnail")
            .bookTitle("book title")
            .bookPublisher("book publisher")
            .bookAuthor("book author")
            .build();

        postRequest = PostRequest.builder()
            .accountRequest(accountRequest)
            .bookRequest(bookRequest)
            .title("post title")
            .description("post description")
            .bookStatus("BEST")
            .price("5000")
            .build();
    }

    @DisplayName("중고책 게시글을 등록한다.")
    @Test
    void createPost() {
        String jwt = UserAcceptanceStep.requestToLoginAndGetAccessToken(loginRequest);
        ExtractableResponse<Response> response = PostAcceptanceStep.requestToCreatePost(jwt,
            postRequest);

        AcceptanceStep.assertThatStatusIsCreated(response);
    }

    @DisplayName("중고책 게시글을 상세 조회한다.")
    @Test
    void findPost() {
        String jwt = UserAcceptanceStep.requestToLoginAndGetAccessToken(loginRequest);

        Long postId = AcceptanceUtils.getIdFromResponse(
            PostAcceptanceStep.requestToCreatePost(jwt, postRequest));

        ExtractableResponse<Response> response = PostAcceptanceStep.requestToFindPost(jwt, postId);
        PostResponse postResponse = response.jsonPath().getObject(".", PostResponse.class);

        AcceptanceStep.assertThatStatusIsOk(response);
        PostAcceptanceStep.assertThatFindPost(postResponse, postRequest);
    }
}
