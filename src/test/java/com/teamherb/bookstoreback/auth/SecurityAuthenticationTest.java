package com.teamherb.bookstoreback.auth;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.teamherb.bookstoreback.common.acceptance.AcceptanceTest;
import com.teamherb.bookstoreback.common.acceptance.step.AcceptanceStep;
import com.teamherb.bookstoreback.user.acceptance.step.UserAcceptanceStep;
import com.teamherb.bookstoreback.user.dto.LoginRequest;
import com.teamherb.bookstoreback.user.dto.LoginResponse;
import com.teamherb.bookstoreback.user.dto.UserResponse;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("시큐리티 인증 테스트")
public class SecurityAuthenticationTest extends AcceptanceTest {

    @DisplayName("옳바른 현재 유저 정보(@CurrentUser)를 가지고 있다.")
    @Test
    void haveValidCurrentUserInfo() {
        LoginRequest loginRequest = new LoginRequest("highright96", "1234");
        LoginResponse loginResponse = UserAcceptanceStep.requestToLogin(loginRequest).jsonPath()
            .getObject(".", LoginResponse.class);

        ExtractableResponse<Response> response = requestToAccessTest(loginResponse);
        UserResponse userResponse = response.jsonPath().getObject(".", UserResponse.class);

        AcceptanceStep.assertThatStatusIsOk(response);
        assertThatAccessTest(userResponse);
    }

    private ExtractableResponse<Response> requestToAccessTest(LoginResponse loginResponse) {
        return given().log().all()
            .header("jwt", loginResponse.getTokenType() + " " + loginResponse.getAccessToken())
            .when()
            .get("/api/user/access-test")
            .then().log().all()
            .extract();
    }

    private void assertThatAccessTest(UserResponse userResponse) {
        Assertions.assertAll(
            () -> assertThat(userResponse.getIdentity()).isEqualTo(user.getIdentity()),
            () -> assertThat(userResponse.getEmail()).isEqualTo(user.getEmail()),
            () -> assertThat(userResponse.getName()).isEqualTo(user.getName())
        );
    }
}
