package com.teamherb.bookstoreback.post.acceptance.step;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.teamherb.bookstoreback.common.Pagination;
import com.teamherb.bookstoreback.post.domain.PostStatus;
import com.teamherb.bookstoreback.post.dto.FullPostRequest;
import com.teamherb.bookstoreback.post.dto.FullPostResponse;
import com.teamherb.bookstoreback.post.dto.PostRequest;
import com.teamherb.bookstoreback.post.dto.PostResponse;
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

    public static void assertThatFindPost(PostResponse res, PostRequest req) {
        Assertions.assertAll(
            () -> assertThat(res.getTitle()).isEqualTo(req.getTitle()),
            () -> assertThat(res.getPostStatus()).isEqualTo(PostStatus.SALE),
            () -> assertThat(res.isMyPost()).isEqualTo(true),
            () -> assertThat(res.getAccountResponse().getAccountBank()).isEqualTo(
                req.getAccountRequest().getAccountBank()),
            () -> assertThat(res.getBookResponse().getBookIsbn()).isEqualTo(
                req.getBookRequest().getBookIsbn())
        );
    }

    public static void assertThatFindPosts(List<FullPostResponse> res, PostRequest req) {
        Assertions.assertAll(
            () -> assertThat(res.size()).isEqualTo(1),
            () -> assertThat(res.get(0).getPostPrice()).isEqualTo(req.getPrice()),
            () -> assertThat(res.get(0).getPostTitle()).isEqualTo(req.getTitle()),
            () -> assertThat(res.get(0).getPostPrice()).isEqualTo(req.getPrice()),
            () -> assertThat(res.get(0).getPostStatus()).isEqualTo(PostStatus.SALE),
            () -> assertThat(res.get(0).getBookTitle()).isEqualTo(
                req.getBookRequest().getBookTitle()),
            () -> assertThat(res.get(0).getBookThumbnail()).isEqualTo(
                req.getBookRequest().getBookThumbnail())
        );
    }

    public static void assertThatFindNaverBooks(BookResponse bookResponse) {
        Assertions.assertAll(
            () -> assertThat(bookResponse.getBookTitle()).isEqualTo(
                "토비의 스프링 3.1 세트 (스프링의 이해와 원리 + 스프링의 기술과 선택, 전2권)"),
            () -> assertThat(bookResponse.getBookAuthor()).isEqualTo("이일민")
        );
    }

    public static ExtractableResponse<Response> requestToCreatePost(String jwt,
        PostRequest postRequest) {
        MultiPartSpecification image = new MultiPartSpecBuilder(
            "image".getBytes())
            .mimeType(MimeTypeUtils.IMAGE_JPEG.toString())
            .controlName("images")
            .fileName("image.jpg")
            .build();

        MultiPartSpecification json = new MultiPartSpecBuilder(postRequest)
            .controlName("postRequest")
            .charset(StandardCharsets.UTF_8)
            .mimeType(MimeTypeUtils.APPLICATION_JSON_VALUE).build();

        return given().log().all()
            .header(HttpHeaders.AUTHORIZATION, jwt)
            .contentType(MediaType.MULTIPART_MIXED_VALUE)
            .multiPart(image)
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

    public static ExtractableResponse<Response> requestToFindPosts(String jwt, FullPostRequest req,
        Pagination page) {
        String path = "/api/post"
            + (req.getTitle() != null ? "?title=" + req.getTitle() : "")
            + (req.getAuthor() != null ? "?author=" + req.getAuthor() : "")
            + (req.getPublisher() != null ? "?publisher=" + req.getPublisher() : "")
            + "&page=" + page.getPage()
            + "&size=" + page.getSize();

        return given().log().all()
            .header(HttpHeaders.AUTHORIZATION, jwt)
            .when()
            .get(path)
            .then().log().all()
            .extract();
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
