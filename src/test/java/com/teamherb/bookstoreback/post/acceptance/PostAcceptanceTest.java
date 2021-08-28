package com.teamherb.bookstoreback.post.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import com.teamherb.bookstoreback.account.dto.AccountRequest;
import com.teamherb.bookstoreback.common.Pagination;
import com.teamherb.bookstoreback.common.acceptance.AcceptanceTest;
import com.teamherb.bookstoreback.common.acceptance.AcceptanceUtils;
import com.teamherb.bookstoreback.common.acceptance.step.AcceptanceStep;
import com.teamherb.bookstoreback.post.acceptance.step.PostAcceptanceStep;
import com.teamherb.bookstoreback.post.dto.BookRequest;
import com.teamherb.bookstoreback.post.dto.BookResponse;
import com.teamherb.bookstoreback.post.dto.FullPostRequest;
import com.teamherb.bookstoreback.post.dto.FullPostResponse;
import com.teamherb.bookstoreback.post.dto.NaverBookRequest;
import com.teamherb.bookstoreback.post.dto.PostRequest;
import com.teamherb.bookstoreback.post.dto.PostResponse;
import com.teamherb.bookstoreback.user.acceptance.step.UserAcceptanceStep;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
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
        .bookTitle("토비의 스프링")
        .bookPublisher("허브출판사")
        .bookAuthor("이일민")
        .build();

    postRequest = PostRequest.builder()
        .accountRequest(accountRequest)
        .bookRequest(bookRequest)
        .title("토비의 스프링 팝니다~")
        .description("책 설명")
        .bookStatus("BEST")
        .price("5000")
        .build();
  }

  @DisplayName("게시글을 등록한다.")
  @Test
  void createPost() {
    String jwt = UserAcceptanceStep.requestToLoginAndGetAccessToken(loginRequest);
    ExtractableResponse<Response> response = PostAcceptanceStep.requestToCreatePost(jwt,
        postRequest);

    AcceptanceStep.assertThatStatusIsCreated(response);
  }

  @DisplayName("게시글을 상세 조회한다.")
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

  @DisplayName("게시글을 전체 조회한다.")
  @Test
  void findPosts() {
    FullPostRequest fullPostRequest = FullPostRequest.builder()
        .title("스프링")
        .build();
    Pagination pagination = new Pagination(0, 10);

    String jwt = UserAcceptanceStep.requestToLoginAndGetAccessToken(loginRequest);
    PostAcceptanceStep.requestToCreatePost(jwt, postRequest);

    ExtractableResponse<Response> response = PostAcceptanceStep.requestToFindPosts(jwt,
        fullPostRequest, pagination);
    List<FullPostResponse> fullPostResponses = response.jsonPath()
        .getList(".", FullPostResponse.class);

    AcceptanceStep.assertThatStatusIsOk(response);
    PostAcceptanceStep.assertThatFindPosts(fullPostResponses, postRequest);
  }

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
