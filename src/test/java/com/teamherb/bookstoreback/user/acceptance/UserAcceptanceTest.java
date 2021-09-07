package com.teamherb.bookstoreback.user.acceptance;

import com.teamherb.bookstoreback.account.dto.AccountRequest;
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
    AccountRequest accountRequest = AccountRequest.builder()
        .accountBank("국민은행")
        .accountNumber("123-1234-12345")
        .accountOwner("남상우")
        .build();

    SignUpRequest signUpRequest = SignUpRequest.builder()
        .identity("highright9696")
        .password("1234")
        .name("남상우")
        .email("highright96@email.com")
        .accountRequest(accountRequest)
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
        .phoneNumber("010-0000-0000")
        .address("경기")
        .build();

    String jwt = UserAcceptanceStep.requestToLoginAndGetAccessToken(loginRequest);

    ExtractableResponse<Response> response = UserAcceptanceStep.requestToUpdateMyInfo(jwt,
        userUpdateRequest);
    UserResponse userResponse = UserAcceptanceStep.requestToGetMyInfo(jwt).jsonPath()
        .getObject(".", UserResponse.class);

    AcceptanceStep.assertThatStatusIsOk(response);
    UserAcceptanceStep.assertThatUpdateMyInfo(userResponse, userUpdateRequest);
  }

  @DisplayName("구매내역을 조회한다.")
  @Test
  void findPurchaseHistories() {
    String jwt = UserAcceptanceStep.requestToLoginAndGetAccessToken(loginRequest);
    ExtractableResponse<Response> response = UserAcceptanceStep.requestToFindPurchaseHistories(
        jwt);
    AcceptanceStep.assertThatStatusIsOk(response);
  }

  @DisplayName("판매내역을 조회한다.")
  @Test
  void findSaleHistories() {
    String jwt = UserAcceptanceStep.requestToLoginAndGetAccessToken(loginRequest);
    ExtractableResponse<Response> response = UserAcceptanceStep.requestToFindSaleOrders(
        jwt);
    AcceptanceStep.assertThatStatusIsOk(response);
  }


  @DisplayName("판매자 주문정보를 조회한다.")
  @Test
  void findSaleOrders() {
    String jwt = UserAcceptanceStep.requestToLoginAndGetAccessToken(loginRequest);
    ExtractableResponse<Response> response = UserAcceptanceStep.requestToFindSaleHistories(
        jwt);
    AcceptanceStep.assertThatStatusIsOk(response);
  }

  @DisplayName("구매자 주문정보를 조회한다.")
  @Test
  void findPurchaseOrders() {
    String jwt = UserAcceptanceStep.requestToLoginAndGetAccessToken(loginRequest);
    ExtractableResponse<Response> response = UserAcceptanceStep.requestToFindPurchaseOrders(
        jwt);
    AcceptanceStep.assertThatStatusIsOk(response);
  }

  @DisplayName("판매 게시글을 조회한다.")
  @Test
  void findSalePosts() {
    String jwt = UserAcceptanceStep.requestToLoginAndGetAccessToken(loginRequest);
    ExtractableResponse<Response> response = UserAcceptanceStep.requestToFindSalePosts(
        jwt);
    AcceptanceStep.assertThatStatusIsOk(response);
  }

  @DisplayName("관심목록을 조회한다.")
  @Test
  void findBaskets() {
    String jwt = UserAcceptanceStep.requestToLoginAndGetAccessToken(loginRequest);
    ExtractableResponse<Response> response = UserAcceptanceStep.requestToFindBaskets(
        jwt);
    AcceptanceStep.assertThatStatusIsOk(response);
  }
}
