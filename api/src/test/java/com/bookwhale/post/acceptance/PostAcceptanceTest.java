package com.bookwhale.post.acceptance;

import com.bookwhale.common.acceptance.AcceptanceTest;
import com.bookwhale.common.acceptance.AcceptanceUtils;
import com.bookwhale.common.acceptance.step.AcceptanceStep;
import com.bookwhale.dto.Pagination;
import com.bookwhale.post.acceptance.step.PostAcceptanceStep;
import com.bookwhale.post.domain.BookStatus;
import com.bookwhale.post.domain.PostStatus;
import com.bookwhale.post.dto.*;
import com.bookwhale.user.acceptance.step.UserAcceptanceStep;
import com.bookwhale.user.dto.LikeRequest;
import io.restassured.builder.MultiPartSpecBuilder;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.MultiPartSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.util.MimeTypeUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("게시글 통합 테스트")
public class PostAcceptanceTest extends AcceptanceTest {

  PostRequest postRequest;

  @BeforeEach
  @Override
  public void setUp() {
    super.setUp();
    BookRequest toby = BookRequest.builder()
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
        .bookRequest(toby)
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
    ExtractableResponse<Response> res = PostAcceptanceStep.requestToCreatePost(jwt, postRequest);
    AcceptanceStep.assertThatStatusIsCreated(res);
  }

  @DisplayName("게시글을 상세 조회한다. (나의 게시글, 관심목록 X)")
  @Test
  void findPost_isMyPostAndIsNotMyLike() {
    String jwt = UserAcceptanceStep.requestToLoginAndGetAccessToken(loginRequest);

    Long postId = AcceptanceUtils.getIdFromResponse(
        PostAcceptanceStep.requestToCreatePost(jwt, postRequest));

    ExtractableResponse<Response> response = PostAcceptanceStep.requestToFindPost(jwt, postId);
    PostResponse postResponse = response.jsonPath().getObject(".", PostResponse.class);

    AcceptanceStep.assertThatStatusIsOk(response);
    PostAcceptanceStep.assertThatFindPost(postResponse, postRequest, user, true, false);
  }

  @DisplayName("게시글을 상세 조회한다. (다른 유저의 게시글, 관심목록 O)")
  @Test
  void findPost_isNotMyPostAndIsMyLike() {
    String anotherUserJwt = UserAcceptanceStep.requestToLoginAndGetAccessToken(anotherLoginRequest);
    Long postId = AcceptanceUtils.getIdFromResponse(
        PostAcceptanceStep.requestToCreatePost(anotherUserJwt, postRequest));

    String jwt = UserAcceptanceStep.requestToLoginAndGetAccessToken(loginRequest);
    UserAcceptanceStep.addLike(jwt, new LikeRequest(postId));

    ExtractableResponse<Response> response = PostAcceptanceStep.requestToFindPost(jwt, postId);
    PostResponse postResponse = response.jsonPath().getObject(".", PostResponse.class);

    AcceptanceStep.assertThatStatusIsOk(response);
    PostAcceptanceStep.assertThatFindPost(postResponse, postRequest, anotherUser, false, true);
  }

  @DisplayName("로그인한 유저가 게시글을 전체 조회한다.")
  @Test
  void findPosts_loginUser() {
    PostsRequest postsRequest = PostsRequest.builder()
        .title("스프링")
        .build();

    Pagination pagination = new Pagination(0, 10);

    String jwt = UserAcceptanceStep.requestToLoginAndGetAccessToken(loginRequest);
    PostAcceptanceStep.requestToCreatePost(jwt, postRequest);

    ExtractableResponse<Response> response = PostAcceptanceStep.requestToFindPosts(jwt,
        postsRequest, pagination);
    List<PostsResponse> postsResponses = response.jsonPath()
        .getList(".", PostsResponse.class);

    AcceptanceStep.assertThatStatusIsOk(response);
    PostAcceptanceStep.assertThatFindPosts(postsResponses, postRequest);
  }

  @DisplayName("로그인하지 않은 유저가 게시글을 전체 조회한다.")
  @Test
  void findPosts_anonymousUser() {
    PostsRequest postsRequest = PostsRequest.builder()
        .title("스프링")
        .build();

    Pagination pagination = new Pagination(0, 10);

    String jwt = UserAcceptanceStep.requestToLoginAndGetAccessToken(loginRequest);
    PostAcceptanceStep.requestToCreatePost(jwt, postRequest);

    ExtractableResponse<Response> response = PostAcceptanceStep.requestToFindPosts("anonymousUser",
        postsRequest, pagination);
    List<PostsResponse> postsResponses = response.jsonPath()
        .getList(".", PostsResponse.class);

    AcceptanceStep.assertThatStatusIsOk(response);
    PostAcceptanceStep.assertThatFindPosts(postsResponses, postRequest);
  }

  @DisplayName("ISBN 으로 네이버 책(API)을 검색한다.")
  @Test
  void findNaverBooks_isbn() {
    NaverBookRequest naverBookRequest = NaverBookRequest.builder()
        .isbn("8960773433")
        .display(10)
        .start(1)
        .build();

    String jwt = UserAcceptanceStep.requestToLoginAndGetAccessToken(loginRequest);

    ExtractableResponse<Response> response = PostAcceptanceStep.requestToFindNaverBooks(jwt,
        naverBookRequest);
    BookResponse bookResponse = response.jsonPath().getList(".", BookResponse.class).get(0);

    AcceptanceStep.assertThatStatusIsOk(response);
    PostAcceptanceStep.assertThatFindNaverBooks(bookResponse);
  }

  @DisplayName("제목으로 네이버 책(API)을 검색한다.")
  @Test
  void findNaverBooks_title() {
    NaverBookRequest naverBookRequest = NaverBookRequest.builder()
        .title("토비")
        .display(10)
        .start(1)
        .build();

    String jwt = UserAcceptanceStep.requestToLoginAndGetAccessToken(loginRequest);

    ExtractableResponse<Response> response = PostAcceptanceStep.requestToFindNaverBooks(jwt,
        naverBookRequest);
    List<BookResponse> bookResponses = response.jsonPath().getList(".", BookResponse.class);

    AcceptanceStep.assertThatStatusIsOk(response);
    assertThat(bookResponses.size()).isEqualTo(naverBookRequest.getDisplay());
  }

  @DisplayName("게시글을 수정한다.")
  @Test
  void updatePost() {
    String jwt = UserAcceptanceStep.requestToLoginAndGetAccessToken(loginRequest);

    Long postId = AcceptanceUtils.getIdFromResponse(
        PostAcceptanceStep.requestToCreatePost(jwt, postRequest));
    String deleteImgUrl = PostAcceptanceStep.requestToFindPost(jwt, postId).jsonPath()
        .getObject(".", PostResponse.class).getImages().get(0);

    PostUpdateRequest updateRequest = PostUpdateRequest.builder()
        .title("토비의 스프링 팝니다~ (수정)")
        .description("책 설명 (수정)")
        .bookStatus(BookStatus.MIDDLE.toString())
        .price("25000")
        .deleteImgUrls(List.of(deleteImgUrl))
        .build();

    MultiPartSpecification image = new MultiPartSpecBuilder(
        "updateImage1".getBytes())
        .mimeType(MimeTypeUtils.IMAGE_JPEG.toString())
        .controlName("images")
        .fileName("updateImage1.jpg")
        .build();
    List<MultiPartSpecification> images = List.of(image);

    ExtractableResponse<Response> response = PostAcceptanceStep.requestToUpdatePost(jwt, postId,
        updateRequest, images);

    PostResponse postResponse = PostAcceptanceStep.requestToFindPost(jwt, postId).jsonPath()
        .getObject(".", PostResponse.class);

    AcceptanceStep.assertThatStatusIsOk(response);
    PostAcceptanceStep.assertThatUpdatePost(postResponse, updateRequest, 2);
  }

  @DisplayName("게시글 상태를 변경한다.")
  @Test
  void updatePostStatus() {
    PostStatusUpdateRequest request = new PostStatusUpdateRequest(PostStatus.RESERVED.toString());

    String jwt = UserAcceptanceStep.requestToLoginAndGetAccessToken(loginRequest);
    Long postId = AcceptanceUtils.getIdFromResponse(
        PostAcceptanceStep.requestToCreatePost(jwt, postRequest));

    ExtractableResponse<Response> response = PostAcceptanceStep.requestToUpdatePostStatus(
        jwt, postId, request);
    String postStatus = PostAcceptanceStep.requestToFindPost(jwt, postId).jsonPath()
        .getObject(".", PostResponse.class).getPostStatus();

    AcceptanceStep.assertThatStatusIsOk(response);
    assertThat(postStatus).isEqualTo(PostStatus.valueOf(request.getPostStatus()).getName());
  }

  @DisplayName("게시글을 삭제한다.")
  @Test
  void deletePost() {
    String jwt = UserAcceptanceStep.requestToLoginAndGetAccessToken(loginRequest);
    Long postId = AcceptanceUtils.getIdFromResponse(
        PostAcceptanceStep.requestToCreatePost(jwt, postRequest));

    ExtractableResponse<Response> response = PostAcceptanceStep.requestToDeletePost(jwt, postId);
    List<PostsResponse> postsResponses = PostAcceptanceStep.requestToFindPosts(
            jwt, new PostsRequest(), new Pagination(0, 10))
        .jsonPath().getList(".", PostsResponse.class);

    AcceptanceStep.assertThatStatusIsOk(response);
    assertThat(postsResponses.size()).isEqualTo(0);
  }
}
