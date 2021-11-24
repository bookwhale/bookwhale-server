package com.bookwhale.user.acceptance.step;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.bookwhale.post.domain.PostStatus;
import com.bookwhale.post.dto.PostRequest;
import com.bookwhale.post.dto.PostsResponse;
import com.bookwhale.user.domain.User;
import com.bookwhale.user.dto.LikeRequest;
import com.bookwhale.user.dto.LikeResponse;
import com.bookwhale.user.dto.LoginRequest;
import com.bookwhale.user.dto.LoginResponse;
import com.bookwhale.user.dto.PasswordUpdateRequest;
import com.bookwhale.user.dto.ProfileResponse;
import com.bookwhale.user.dto.SignUpRequest;
import com.bookwhale.user.dto.UserResponse;
import com.bookwhale.user.dto.UserUpdateRequest;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.MultiPartSpecification;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public class UserAcceptanceStep {

    public static void assertThatLogin(LoginResponse loginResponse) {
        Assertions.assertAll(
            () -> assertThat(loginResponse.getAccessToken()).isNotNull(),
            () -> assertThat(loginResponse.getTokenType()).isNotNull()
        );
    }

    public static void assertThatGetMyInfo(UserResponse userResponse, User user) {
        Assertions.assertAll(
            () -> assertThat(userResponse.getIdentity()).isEqualTo(user.getIdentity()),
            () -> assertThat(userResponse.getName()).isEqualTo(user.getName()),
            () -> assertThat(userResponse.getPhoneNumber()).isEqualTo(user.getPhoneNumber()),
            () -> assertThat(userResponse.getEmail()).isEqualTo(user.getEmail())
        );
    }

    public static void assertThatUpdateMyInfo(UserResponse res, UserUpdateRequest req) {
        Assertions.assertAll(
            () -> assertThat(res.getEmail()).isEqualTo(req.getEmail()),
            () -> assertThat(res.getName()).isEqualTo(req.getName()),
            () -> assertThat(res.getPhoneNumber()).isEqualTo(req.getPhoneNumber())
        );
    }

    public static void assertThatUploadProfileImage(ProfileResponse profileResponse,
        UserResponse userResponse) {
        assertThat(profileResponse.getProfileImage()).isEqualTo(userResponse.getProfileImage());
    }

    public static void assertThatDeleteProfileImage(UserResponse res) {
        assertThat(res.getProfileImage()).isNull();
    }

    public static void assertThatAddLike(List<LikeResponse> res, PostRequest req) {
        Assertions.assertAll(
            () -> assertThat(res.size()).isEqualTo(1),
            () -> assertThat(
                res.get(0).getPostsResponse().getPostTitle()).isEqualTo(req.getTitle()),
            () -> assertThat(
                res.get(0).getPostsResponse().getPostPrice()).isEqualTo(req.getPrice()),
            () -> assertThat(
                res.get(0).getPostsResponse().getPostImage()).isNotNull(),
            () -> assertThat(
                res.get(0).getPostsResponse().getBeforeTime()).isNotNull(),
            () -> assertThat(
                res.get(0).getPostsResponse().getLikeCount()).isGreaterThan(0L)
        );
    }

    public static void assertThatFindMyPosts(List<PostsResponse> res, PostRequest req) {
        Assertions.assertAll(
            () -> assertThat(res.size()).isEqualTo(1),
            () -> assertThat(res.get(0).getPostTitle()).isEqualTo(req.getTitle()),
            () -> assertThat(res.get(0).getPostPrice()).isEqualTo(req.getPrice()),
            () -> assertThat(res.get(0).getPostStatus()).isEqualTo(PostStatus.SALE.getName()),
            () -> assertThat(res.get(0).getPostImage()).isNotNull(),
            () -> assertThat(res.get(0).getBeforeTime()).isNotNull()
        );
    }

    public static ExtractableResponse<Response> requestToSignUp(SignUpRequest signUpRequest) {
        return given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(signUpRequest)
            .when()
            .post("/api/user/signup")
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> requestToLogin(LoginRequest loginRequest) {
        return given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(loginRequest)
            .when()
            .post("/api/user/login")
            .then().log().all()
            .extract();
    }

    public static String requestToLoginAndGetAccessToken(LoginRequest loginRequest) {
        LoginResponse loginResponse = requestToLogin(loginRequest).jsonPath()
            .getObject(".", LoginResponse.class);
        return loginResponse.getTokenType() + " " + loginResponse.getAccessToken();

    }

    public static ExtractableResponse<Response> requestToGetMyInfo(String jwt) {
        return given().log().all()
            .header(HttpHeaders.AUTHORIZATION, jwt)
            .when()
            .get("/api/user/me")
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> requestToUpdateMyInfo(String jwt,
        UserUpdateRequest userUpdateRequest) {
        return given().log().all()
            .header(HttpHeaders.AUTHORIZATION, jwt)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(userUpdateRequest)
            .when()
            .patch("/api/user/me")
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> requestToUpdatePassword(String jwt,
        PasswordUpdateRequest request) {
        return given().log().all()
            .header(HttpHeaders.AUTHORIZATION, jwt)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(request)
            .when()
            .patch("/api/user/password")
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> uploadProfileImage(String jwt,
        MultiPartSpecification image) {
        return given().log().all()
            .header(HttpHeaders.AUTHORIZATION, jwt)
            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
            .multiPart(image)
            .when()
            .patch("/api/user/profile")
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> deleteProfileImage(String jwt) {
        return given().log().all()
            .header(HttpHeaders.AUTHORIZATION, jwt)
            .when()
            .delete("/api/user/profile")
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> addLike(String jwt, LikeRequest request) {
        return given().log().all()
            .header(HttpHeaders.AUTHORIZATION, jwt)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(request)
            .when()
            .post("/api/user/me/like")
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> findLikes(String jwt) {
        return given().log().all()
            .header(HttpHeaders.AUTHORIZATION, jwt)
            .when()
            .get("/api/user/me/likes")
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> deleteLike(String jwt, Long likeId) {
        return given().log().all()
            .header(HttpHeaders.AUTHORIZATION, jwt)
            .when()
            .delete("/api/user/me/like/{likeId}", likeId)
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> requestToFindMyPosts(String jwt) {
        return given().log().all()
            .header(HttpHeaders.AUTHORIZATION, jwt)
            .when()
            .get("/api/user/me/post")
            .then().log().all()
            .extract();
    }
}