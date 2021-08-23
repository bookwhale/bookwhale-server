package com.teamherb.bookstoreback.post.acceptance;

import com.teamherb.bookstoreback.account.dto.AccountRequest;
import com.teamherb.bookstoreback.common.acceptance.AcceptanceTest;
import com.teamherb.bookstoreback.common.acceptance.step.AcceptanceStep;
import com.teamherb.bookstoreback.post.acceptance.step.PostAcceptanceStep;
import com.teamherb.bookstoreback.post.dto.BookRequest;
import com.teamherb.bookstoreback.post.dto.PostRequest;
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
            .accountBank("국민은행")
            .accountOwner("남상우")
            .accountNumber("123-1234-12345")
            .build();

        BookRequest bookRequest = BookRequest.builder()
            .bookSummary("책 설명")
            .bookPubDate("2021-12-12")
            .bookIsbn("12345678910")
            .bookListPrice("10000")
            .bookThumbnail("썸네일")
            .bookTitle("책 제목")
            .bookPublisher("출판사")
            .bookAuthor("작가")
            .build();

        postRequest = PostRequest.builder()
            .accountRequest(accountRequest)
            .bookRequest(bookRequest)
            .title("책 팝니다~")
            .description("쿨 거래시 1000원 할인해드려요~")
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
}
