package com.bookwhale.user.acceptance.step;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.bookwhale.article.dto.ArticleRequest;
import com.bookwhale.auth.domain.JWT;
import com.bookwhale.auth.domain.info.UserInfo;
import com.bookwhale.auth.dto.OAuthObjectConverter;
import com.bookwhale.user.domain.User;
import com.bookwhale.user.dto.FavoriteRequest;
import com.bookwhale.user.dto.FavoriteResponse;
import com.bookwhale.user.dto.LoginRequest;
import com.bookwhale.user.dto.PasswordUpdateRequest;
import com.bookwhale.user.dto.ProfileResponse;
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

    public static void assertThatGetMyInfo(UserResponse userResponse, User user) {
        Assertions.assertAll(
            () -> assertThat(userResponse.getNickName()).isEqualTo(user.getNickname())
        );
    }

    public static void assertThatUpdateMyInfo(UserResponse res, UserUpdateRequest req) {
        Assertions.assertAll(
            () -> assertThat(res.getNickName()).isEqualTo(req.getNickname())
        );
    }

    public static void assertThatUploadProfileImage(ProfileResponse profileResponse,
        UserResponse userResponse) {
        assertThat(profileResponse.getProfileImage()).isEqualTo(userResponse.getProfileImage());
    }

    public static void assertThatDeleteProfileImage(UserResponse res) {
        assertThat(res.getProfileImage()).isNull();
    }

    public static void assertThatAddFavorite(List<FavoriteResponse> res, ArticleRequest req) {
        Assertions.assertAll(
            () -> assertThat(res.size()).isEqualTo(1),
            () -> assertThat(
                res.get(0).getArticlesResponse().getArticleTitle()).isEqualTo(req.getTitle()),
            () -> assertThat(
                res.get(0).getArticlesResponse().getArticlePrice()).isEqualTo(req.getPrice()),
            () -> assertThat(
                res.get(0).getArticlesResponse().getArticleImage()).isNotNull(),
            () -> assertThat(
                res.get(0).getArticlesResponse().getBeforeTime()).isNotNull(),
            () -> assertThat(
                res.get(0).getArticlesResponse().getFavoriteCount()).isGreaterThan(0L)
        );
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

    public static String requestToLoginAndGetAccessToken(UserInfo userInfo, JWT jwt) {
        String tokenPrefix = "Bearer ";
        return tokenPrefix + OAuthObjectConverter.createApiToken(jwt, userInfo);
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

    public static ExtractableResponse<Response> addFavorite(String jwt, FavoriteRequest request) {
        return given().log().all()
            .header(HttpHeaders.AUTHORIZATION, jwt)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(request)
            .when()
            .post("/api/user/me/favorite")
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> findFavorites(String jwt) {
        return given().log().all()
            .header(HttpHeaders.AUTHORIZATION, jwt)
            .when()
            .get("/api/user/me/favorites")
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> deleteFavorite(String jwt, Long favoriteId) {
        return given().log().all()
            .header(HttpHeaders.AUTHORIZATION, jwt)
            .when()
            .delete("/api/user/me/favorite/{favoriteId}", favoriteId)
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> requestToGetPushSetting(String jwt) {
        return given().log().all()
            .header(HttpHeaders.AUTHORIZATION, jwt)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("/api/user/me/push-setting")
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> requestToTogglePushSetting(String jwt) {
        return given().log().all()
            .header(HttpHeaders.AUTHORIZATION, jwt)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .patch("/api/user/me/push-setting")
            .then().log().all()
            .extract();
    }
}
