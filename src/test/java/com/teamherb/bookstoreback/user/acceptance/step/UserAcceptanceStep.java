package com.teamherb.bookstoreback.user.acceptance.step;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.teamherb.bookstoreback.user.domain.User;
import com.teamherb.bookstoreback.user.dto.LoginRequest;
import com.teamherb.bookstoreback.user.dto.LoginResponse;
import com.teamherb.bookstoreback.user.dto.PasswordUpdateRequest;
import com.teamherb.bookstoreback.user.dto.ProfileResponse;
import com.teamherb.bookstoreback.user.dto.SignUpRequest;
import com.teamherb.bookstoreback.user.dto.UserResponse;
import com.teamherb.bookstoreback.user.dto.UserUpdateRequest;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.MultiPartSpecification;
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

  public static void assertThatUpdateMyInfo(UserResponse userResponse,
      UserUpdateRequest userUpdateRequest) {
    Assertions.assertAll(
        () -> assertThat(userResponse.getEmail()).isEqualTo(userUpdateRequest.getEmail()),
        () -> assertThat(userResponse.getName()).isEqualTo(userUpdateRequest.getName()),
        () -> assertThat(userResponse.getPhoneNumber()).isEqualTo(
            userUpdateRequest.getPhoneNumber())
    );
  }

  public static void assertThatUploadProfileImage(ProfileResponse profileResponse,
      UserResponse userResponse) {
    assertThat(profileResponse.getProfileImage()).isEqualTo(userResponse.getProfileImage());
  }

  public static void assertThatDeleteProfileImage(UserResponse res) {
    assertThat(res.getProfileImage()).isNull();
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
}
