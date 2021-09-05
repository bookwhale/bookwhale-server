package com.teamherb.bookstoreback.user.acceptance;

import com.teamherb.bookstoreback.common.acceptance.AcceptanceTest;
import com.teamherb.bookstoreback.common.acceptance.step.AcceptanceStep;
import com.teamherb.bookstoreback.user.acceptance.step.UserAcceptanceStep;
import com.teamherb.bookstoreback.user.dto.SignUpRequest;
import com.teamherb.bookstoreback.user.dto.UserResponse;
import com.teamherb.bookstoreback.user.dto.UserUpdateRequest;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("유저 통합 테스트")
public class UserAcceptanceTest extends AcceptanceTest {

  @DisplayName("회원가입을 한다.")
  @Test
  void signUpTest() {
    SignUpRequest signUpRequest = SignUpRequest.builder()
        .identity("highright9696")
        .password("1234")
        .name("남상우")
        .email("highright96@email.com")
        .phoneNumber("010-1234-1234")
        .build();

    ExtractableResponse<Response> response = UserAcceptanceStep.requestToSignUp(signUpRequest);

    AcceptanceStep.assertThatStatusIsCreated(response);
  }

  @DisplayName("내 정보를 조회한다.")
  @Test
  void getMyInfo() {
    String jwt = UserAcceptanceStep.requestToLoginAndGetAccessToken(loginRequest);

    ExtractableResponse<Response> response = UserAcceptanceStep.requestToGetMyInfo(jwt);
    UserResponse userResponse = response.jsonPath().getObject(".", UserResponse.class);

    AcceptanceStep.assertThatStatusIsOk(response);
    UserAcceptanceStep.assertThatGetMyInfo(userResponse, user);
  }

  @DisplayName("내 정보를 수정한다.")
  @Test
  void updateMyInfo() {
    UserUpdateRequest userUpdateRequest = UserUpdateRequest.builder()
        .name("주호세")
        .phoneNumber("010-5678-5678")
        .email("hose@email.com")
        .build();

    String jwt = UserAcceptanceStep.requestToLoginAndGetAccessToken(loginRequest);

    ExtractableResponse<Response> response = UserAcceptanceStep.requestToUpdateMyInfo(jwt,
        userUpdateRequest);
    UserResponse userResponse = UserAcceptanceStep.requestToGetMyInfo(jwt).jsonPath()
        .getObject(".", UserResponse.class);

    AcceptanceStep.assertThatStatusIsOk(response);
    UserAcceptanceStep.assertThatUpdateMyInfo(userResponse, userUpdateRequest);
  }
}
