package com.teamherb.bookstoreback.user.acceptance.step;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.teamherb.bookstoreback.user.dto.LoginRequest;
import com.teamherb.bookstoreback.user.dto.LoginResponse;
import com.teamherb.bookstoreback.user.dto.SignUpRequest;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.MediaType;

public class UserAcceptanceStep {

    public static void assertThatLogin(LoginResponse loginResponse) {
        Assertions.assertAll(
            () -> assertThat(loginResponse.getAccessToken()).isNotNull(),
            () -> assertThat(loginResponse.getTokenType()).isNotNull()
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
}
