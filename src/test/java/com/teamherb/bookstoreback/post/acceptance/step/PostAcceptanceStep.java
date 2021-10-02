package com.teamherb.bookstoreback.post.acceptance.step;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.teamherb.bookstoreback.common.Pagination;
import com.teamherb.bookstoreback.post.domain.BookStatus;
import com.teamherb.bookstoreback.post.domain.PostStatus;
import com.teamherb.bookstoreback.post.dto.BookResponse;
import com.teamherb.bookstoreback.post.dto.NaverBookRequest;
import com.teamherb.bookstoreback.post.dto.PostRequest;
import com.teamherb.bookstoreback.post.dto.PostResponse;
import com.teamherb.bookstoreback.post.dto.PostStatusUpdateRequest;
import com.teamherb.bookstoreback.post.dto.PostUpdateRequest;
import com.teamherb.bookstoreback.post.dto.PostsRequest;
import com.teamherb.bookstoreback.post.dto.PostsResponse;
import com.teamherb.bookstoreback.user.domain.User;
import io.restassured.builder.MultiPartSpecBuilder;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.MultiPartSpecification;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.MimeTypeUtils;

public class PostAcceptanceStep {

  public static void assertThatFindPost(PostResponse res, PostRequest req, User seller,
      boolean isMyPost, boolean isMyInterest) {
    Assertions.assertAll(
        () -> assertThat(res.getSellerId()).isEqualTo(seller.getId()),
        () -> assertThat(res.getSellerIdentity()).isEqualTo(seller.getIdentity()),
        () -> assertThat(res.getSellerProfileImage()).isEqualTo(seller.getProfileImage()),
        () -> assertThat(res.getTitle()).isEqualTo(req.getTitle()),
        () -> assertThat(res.getPrice()).isEqualTo(req.getPrice()),
        () -> assertThat(res.getDescription()).isEqualTo(req.getDescription()),
        () -> assertThat(res.getPostStatus()).isEqualTo(PostStatus.SALE.getName()),
        () -> assertThat(res.getTitle()).isEqualTo(req.getTitle()),
        () -> assertThat(res.isMyPost()).isEqualTo(isMyPost),
        () -> assertThat(res.isMyInterest()).isEqualTo(isMyInterest),
        () -> assertThat(res.getCreatedDate()).isNotNull(),
        () -> assertThat(res.getLastModifiedDate()).isNotNull(),
        () -> assertThat(res.getBookStatus()).isEqualTo(
            BookStatus.valueOf(req.getBookStatus()).getName()),
        () -> assertThat(res.getBookResponse().getBookIsbn()).isEqualTo(
            req.getBookRequest().getBookIsbn()),
        () -> assertThat(res.getBookResponse().getBookAuthor()).isEqualTo(
            req.getBookRequest().getBookAuthor()),
        () -> assertThat(res.getBookResponse().getBookTitle()).isEqualTo(
            req.getBookRequest().getBookTitle()),
        () -> assertThat(res.getBookResponse().getBookPublisher()).isEqualTo(
            req.getBookRequest().getBookPublisher()),
        () -> assertThat(res.getBookResponse().getBookSummary()).isEqualTo(
            req.getBookRequest().getBookSummary()),
        () -> assertThat(res.getBookResponse().getBookThumbnail()).isEqualTo(
            req.getBookRequest().getBookThumbnail()),
        () -> assertThat(res.getBookResponse().getBookPubDate()).isEqualTo(
            req.getBookRequest().getBookPubDate()),
        () -> assertThat(res.getBookResponse().getBookListPrice()).isEqualTo(
            req.getBookRequest().getBookListPrice())
    );
  }

  public static void assertThatFindPosts(List<PostsResponse> res, PostRequest req1,
      PostRequest req2) {
    Assertions.assertAll(
        () -> assertThat(res.size()).isEqualTo(2),
        // 책 이미지가 없는 게시글
        () -> assertThat(res.get(0).getPostPrice()).isEqualTo(req2.getPrice()),
        () -> assertThat(res.get(0).getPostTitle()).isEqualTo(req2.getTitle()),
        () -> assertThat(res.get(0).getPostPrice()).isEqualTo(req2.getPrice()),
        () -> assertThat(res.get(0).getPostStatus()).isEqualTo(PostStatus.SALE.getName()),
        () -> assertThat(res.get(0).getBookTitle()).isEqualTo(req2.getBookRequest().getBookTitle()),
        () -> assertThat(res.get(0).getPostImage()).isNull(),
        // 책 이미지가 있는 게시글
        () -> assertThat(res.get(1).getPostPrice()).isEqualTo(req1.getPrice()),
        () -> assertThat(res.get(1).getPostTitle()).isEqualTo(req1.getTitle()),
        () -> assertThat(res.get(1).getPostPrice()).isEqualTo(req1.getPrice()),
        () -> assertThat(res.get(1).getPostStatus()).isEqualTo(PostStatus.SALE.getName()),
        () -> assertThat(res.get(1).getBookTitle()).isEqualTo(req1.getBookRequest().getBookTitle()),
        () -> assertThat(res.get(1).getPostImage()).isNotNull()
    );
  }

  public static void assertThatFindNaverBooks(BookResponse bookResponse) {
    Assertions.assertAll(
        () -> assertThat(bookResponse.getBookTitle()).isEqualTo(
            "토비의 스프링 3.1 세트 (스프링의 이해와 원리 + 스프링의 기술과 선택, 전2권)"),
        () -> assertThat(bookResponse.getBookAuthor()).isEqualTo("이일민")
    );
  }

  public static void assertThatUpdatePost(PostResponse res, PostUpdateRequest req, int size) {
    Assertions.assertAll(
        () -> assertThat(res.getTitle()).isEqualTo(req.getTitle()),
        () -> assertThat(res.getDescription()).isEqualTo(req.getDescription()),
        () -> assertThat(res.getPrice()).isEqualTo(req.getPrice()),
        () -> assertThat(res.getBookStatus()).isEqualTo(BookStatus.valueOf(req.getBookStatus()).getName()),
        () -> assertThat(res.getImages().size()).isEqualTo(size)
    );
  }

  public static ExtractableResponse<Response> requestToCreatePost(String jwt,
      PostRequest postRequest) {
    MultiPartSpecification image1 = new MultiPartSpecBuilder(
        "image1".getBytes())
        .mimeType(MimeTypeUtils.IMAGE_JPEG.toString())
        .controlName("images")
        .fileName("image1.jpg")
        .build();

    MultiPartSpecification image2 = new MultiPartSpecBuilder(
        "image2".getBytes())
        .mimeType(MimeTypeUtils.IMAGE_JPEG.toString())
        .controlName("images")
        .fileName("image2.jpg")
        .build();

    MultiPartSpecification json = new MultiPartSpecBuilder(postRequest)
        .controlName("postRequest")
        .charset(StandardCharsets.UTF_8)
        .mimeType(MimeTypeUtils.APPLICATION_JSON_VALUE).build();

    return given().log().all()
        .header(HttpHeaders.AUTHORIZATION, jwt)
        .contentType(MediaType.MULTIPART_MIXED_VALUE)
        .multiPart(image1)
        .multiPart(image2)
        .multiPart(json)
        .when()
        .post("/api/post")
        .then().log().all()
        .extract();
  }

  public static void requestToCreateEmptyImagePost(String jwt, PostRequest postRequest) {
    MultiPartSpecification json = new MultiPartSpecBuilder(postRequest)
        .controlName("postRequest")
        .charset(StandardCharsets.UTF_8)
        .mimeType(MimeTypeUtils.APPLICATION_JSON_VALUE).build();

    given().log().all()
        .header(HttpHeaders.AUTHORIZATION, jwt)
        .contentType(MediaType.MULTIPART_MIXED_VALUE)
        .multiPart(json)
        .when()
        .post("/api/post")
        .then().log().all()
        .extract();
  }

  public static ExtractableResponse<Response> requestToFindPost(String jwt, Long postId) {
    return given().log().all()
        .header(HttpHeaders.AUTHORIZATION, jwt)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .when()
        .get("/api/post/{postId}", postId)
        .then().log().all()
        .extract();
  }

  public static ExtractableResponse<Response> requestToFindPosts(String jwt, PostsRequest req,
      Pagination page) {
    return given().log().all()
        .header(HttpHeaders.AUTHORIZATION, jwt)
        .when()
        .get("/api/post?"
            + (req.getTitle() != null ? "title=" + req.getTitle() + "&" : "")
            + (req.getAuthor() != null ? "author=" + req.getAuthor() + "&" : "")
            + (req.getPublisher() != null ? "publisher=" + req.getPublisher() + "&" : "")
            + "page=" + page.getPage()
            + "&size=" + page.getSize())
        .then().log().all()
        .extract();
  }

  public static ExtractableResponse<Response> requestToFindNaverBooks(String jwt,
      NaverBookRequest req) {
    return given().log().all()
        .header(HttpHeaders.AUTHORIZATION, jwt)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .body(req)
        .when()
        .get("/api/post/naverBookAPI?"
            + (req.getTitle() == null ? "" : "title=" + req.getTitle())
            + (req.getIsbn() == null ? "" : "isbn=" + req.getIsbn())
            + (req.getAuthor() == null ? "" : "author=" + req.getAuthor())
            + "&display=" + req.getDisplay()
            + "&start=" + req.getStart()
        )
        .then().log().all()
        .extract();
  }

  public static ExtractableResponse<Response> requestToUpdatePost(String jwt,
      Long postId, PostUpdateRequest request, List<MultiPartSpecification> images) {

    MultiPartSpecification json = new MultiPartSpecBuilder(request)
        .controlName("postUpdateRequest")
        .charset(StandardCharsets.UTF_8)
        .mimeType(MimeTypeUtils.APPLICATION_JSON_VALUE).build();

    return given().log().all()
        .header(HttpHeaders.AUTHORIZATION, jwt)
        .contentType(MediaType.MULTIPART_MIXED_VALUE)
        .multiPart(images.get(0))
        .multiPart(json)
        .when()
        .patch("/api/post/{postId}", postId)
        .then().log().all()
        .extract();
  }

  public static ExtractableResponse<Response> requestToUpdatePostStatus(String jwt, Long postId,
      PostStatusUpdateRequest request) {
    return given().log().all()
        .header(HttpHeaders.AUTHORIZATION, jwt)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .body(request)
        .when()
        .patch("/api/post/postStatus/{postId}", postId)
        .then().log().all()
        .extract();
  }

  public static ExtractableResponse<Response> requestToDeletePost(String jwt, Long postId) {
    return given().log().all()
        .header(HttpHeaders.AUTHORIZATION, jwt)
        .when()
        .delete("/api/post/{postId}", postId)
        .then().log().all()
        .extract();
  }
}
