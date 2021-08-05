package com.teamherb.bookstoreback.user.acceptance;

import com.teamherb.bookstoreback.account.dto.AccountRequest;
import com.teamherb.bookstoreback.common.acceptance.AcceptanceTest;
import com.teamherb.bookstoreback.common.acceptance.step.AcceptanceStep;
import com.teamherb.bookstoreback.user.acceptance.step.UserAcceptanceStep;
import com.teamherb.bookstoreback.user.dto.SignUpRequest;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("유저 통합 테스트")
public class UserAcceptanceTest extends AcceptanceTest {

    @BeforeEach
    @Override
    public void setUp() {
        super.setUp();
    }

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
}
