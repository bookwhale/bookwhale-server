package com.teamherb.bookstoreback.auth;

import static org.assertj.core.api.Assertions.assertThat;

import com.teamherb.bookstoreback.common.acceptance.AcceptanceTest;
import com.teamherb.bookstoreback.user.acceptance.step.UserAcceptanceStep;
import com.teamherb.bookstoreback.user.dto.LoginRequest;
import com.teamherb.bookstoreback.user.dto.LoginResponse;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("시큐리티 로그인 테스트")
public class SecurityLoginTest extends AcceptanceTest {

    @DisplayName("옳바른 로그인 요청을 한다.")
    @Test
    void ValidLoginTest() {
        LoginRequest loginRequest = new LoginRequest("highright96", "1234");
        ExtractableResponse<Response> response = UserAcceptanceStep.requestToLogin(loginRequest);
        LoginResponse loginResponse = response.jsonPath().getObject(".", LoginResponse.class);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        UserAcceptanceStep.assertThatLogin(loginResponse);
    }

    @DisplayName("잘못된 아이디로 로그인 요청을 한다.")
    @Test
    void inValidIdLoginTest() {
        LoginRequest loginRequest = new LoginRequest("user", "1234");
        ExtractableResponse<Response> response = UserAcceptanceStep.requestToLogin(loginRequest);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @DisplayName("잘못된 비밀번호로 로그인 요청을 한다")
    @Test
    void inValidPasswordLoginTest() {
        LoginRequest loginRequest = new LoginRequest("highright96", "12345");
        ExtractableResponse<Response> response = UserAcceptanceStep.requestToLogin(loginRequest);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }
}
