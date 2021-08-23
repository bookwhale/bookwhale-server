package com.teamherb.bookstoreback.post.acceptance.step;

import static io.restassured.RestAssured.given;

import com.teamherb.bookstoreback.post.dto.PostRequest;
import io.restassured.builder.MultiPartSpecBuilder;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.MultiPartSpecification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.MimeTypeUtils;

public class PostAcceptanceStep {

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
}
