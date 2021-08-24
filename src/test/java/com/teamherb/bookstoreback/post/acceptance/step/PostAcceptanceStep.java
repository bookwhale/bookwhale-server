package com.teamherb.bookstoreback.post.acceptance.step;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.teamherb.bookstoreback.post.domain.PostStatus;
import com.teamherb.bookstoreback.post.dto.PostRequest;
import com.teamherb.bookstoreback.post.dto.PostResponse;
import io.restassured.builder.MultiPartSpecBuilder;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.MultiPartSpecification;
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

    public static ExtractableResponse<Response> requestToCreatePost(String jwt,
        PostRequest postRequest) {
        MultiPartSpecification image = new MultiPartSpecBuilder(
            "image".getBytes())
            .mimeType(MimeTypeUtils.IMAGE_JPEG.toString())
            .controlName("images")
            .fileName("image.jpg")
            .build();

        MultiPartSpecification json = new MultiPartSpecBuilder(postRequest).controlName(
                "postRequest")
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
}
