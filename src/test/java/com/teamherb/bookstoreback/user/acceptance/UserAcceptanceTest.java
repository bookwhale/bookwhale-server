package com.teamherb.bookstoreback.user.acceptance;

import com.teamherb.bookstoreback.common.acceptance.AcceptanceTest;
import com.teamherb.bookstoreback.common.acceptance.step.AcceptanceStep;
import com.teamherb.bookstoreback.user.acceptance.step.UserAcceptanceStep;
import com.teamherb.bookstoreback.user.dto.LoginRequest;
import com.teamherb.bookstoreback.user.dto.PasswordUpdateRequest;
import com.teamherb.bookstoreback.user.dto.ProfileResponse;
import com.teamherb.bookstoreback.user.dto.SignUpRequest;
import com.teamherb.bookstoreback.user.dto.UserResponse;
import com.teamherb.bookstoreback.user.dto.UserUpdateRequest;
import io.restassured.builder.MultiPartSpecBuilder;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.MultiPartSpecification;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.util.MimeTypeUtils;

@DisplayName("유저 통합 테스트")
public class UserAcceptanceTest extends AcceptanceTest {

  @DisplayName("회원가입을 한다.")
  @Test
  void signUpTest() {
    SignUpRequest signUpRequest = SignUpRequest.builder()
        .identity("hose12")
        .password("1234")
        .name("주호세")
        .email("hose12@email.com")
        .phoneNumber("010-5678-5678")
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

  @DisplayName("비밀번호를 수정한다.")
  @Test
  void updatePassword() {
    PasswordUpdateRequest req = new PasswordUpdateRequest(loginRequest.getPassword(), "12345");
    LoginRequest newLoginReq = new LoginRequest(loginRequest.getIdentity(), req.getNewPassword());

    String jwt = UserAcceptanceStep.requestToLoginAndGetAccessToken(loginRequest);

    ExtractableResponse<Response> response = UserAcceptanceStep.requestToUpdatePassword(jwt, req);
    ExtractableResponse<Response> newLoginResponse = UserAcceptanceStep.requestToLogin(newLoginReq);

    AcceptanceStep.assertThatStatusIsOk(response);
    AcceptanceStep.assertThatStatusIsOk(newLoginResponse);
  }

  @DisplayName("비밀번호를 수정할 때 기존 비밀번호를 틀리게 입력하면 예외가 발생한다.")
  @Test
  void updatePassword_invalidPassword_failure() {
    PasswordUpdateRequest req = new PasswordUpdateRequest("invalidPassword", "12345");

    String jwt = UserAcceptanceStep.requestToLoginAndGetAccessToken(loginRequest);

    ExtractableResponse<Response> response = UserAcceptanceStep.requestToUpdatePassword(jwt, req);

    AcceptanceStep.assertThatStatusIsBadRequest(response);
  }

  @DisplayName("프로필 사진을 업로드한다.")
  @Test
  void uploadProfileImage() {
    MultiPartSpecification image = new MultiPartSpecBuilder(
        "profileImage".getBytes())
        .mimeType(MimeTypeUtils.IMAGE_JPEG.toString())
        .controlName("profileImage")
        .fileName("profileImage.jpg")
        .build();

    String jwt = UserAcceptanceStep.requestToLoginAndGetAccessToken(loginRequest);

    ExtractableResponse<Response> response = UserAcceptanceStep.uploadProfileImage(jwt, image);
    ProfileResponse profileResponse = response.jsonPath().getObject(".", ProfileResponse.class);
    UserResponse userResponse = UserAcceptanceStep.requestToGetMyInfo(jwt).jsonPath()
        .getObject(".", UserResponse.class);

    AcceptanceStep.assertThatStatusIsOk(response);
    UserAcceptanceStep.assertThatUploadProfileImage(profileResponse, userResponse);
  }

  @DisplayName("프로필 사진을 삭제한다.")
  @Test
  void deleteProfileImage() {
    MultiPartSpecification image = new MultiPartSpecBuilder(
        "profileImage".getBytes())
        .mimeType(MimeTypeUtils.IMAGE_JPEG.toString())
        .controlName("profileImage")
        .fileName("profileImage.jpg")
        .build();

    String jwt = UserAcceptanceStep.requestToLoginAndGetAccessToken(loginRequest);

    UserAcceptanceStep.uploadProfileImage(jwt, image);
    ExtractableResponse<Response> response = UserAcceptanceStep.deleteProfileImage(jwt);
    UserResponse userResponse = UserAcceptanceStep.requestToGetMyInfo(jwt).jsonPath()
        .getObject(".", UserResponse.class);

    AcceptanceStep.assertThatStatusIsOk(response);
    UserAcceptanceStep.assertThatDeleteProfileImage(userResponse);
  }

  @DisplayName("관심목록을 조회한다.")
  @Test
  void findBaskets() {
    String jwt = UserAcceptanceStep.requestToLoginAndGetAccessToken(loginRequest);
    ExtractableResponse<Response> response = UserAcceptanceStep.requestToFindBaskets(
        jwt);
    AcceptanceStep.assertThatStatusIsOk(response);
  }

  @DisplayName("관심목록을 삭제한다.")
  @Test
  void deleteBasket() {
    String jwt = UserAcceptanceStep.requestToLoginAndGetAccessToken(loginRequest);
    ExtractableResponse<Response> response = UserAcceptanceStep.requestDeleteBasket(
        jwt);
    AcceptanceStep.assertThatStatusIsOk(response);
  }
}
