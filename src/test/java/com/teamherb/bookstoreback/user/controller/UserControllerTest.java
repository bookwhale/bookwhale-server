package com.teamherb.bookstoreback.user.controller;

import static java.util.List.of;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.teamherb.bookstoreback.account.domain.Account;
import com.teamherb.bookstoreback.account.dto.AccountRequest;
import com.teamherb.bookstoreback.common.controller.CommonApiTest;
import com.teamherb.bookstoreback.common.security.WithMockCustomUser;
import com.teamherb.bookstoreback.user.docs.UserDocumentation;
import com.teamherb.bookstoreback.user.domain.User;
import com.teamherb.bookstoreback.user.dto.SignUpRequest;
import com.teamherb.bookstoreback.user.dto.UserResponse;
import com.teamherb.bookstoreback.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.web.context.WebApplicationContext;

@DisplayName("유저 단위 테스트(Controller)")
@WebMvcTest(controllers = UserController.class)
public class UserControllerTest extends CommonApiTest {

    @MockBean
    UserService userService;

    User user;

    Account account;

    @BeforeEach
    @Override
    public void setUp(WebApplicationContext webApplicationContext,
        RestDocumentationContextProvider restDocumentation) {
        super.setUp(webApplicationContext, restDocumentation);

        user = User.builder()
            .identity("highright96")
            .name("남상우")
            .email("highright96@email.com")
            .phoneNumber("010-1234-1234")
            .address("서울")
            .build();

        account = Account.builder()
            .user(user)
            .accountBank("국민은행")
            .accountNumber("123-1234-12345")
            .accountOwner("남상우")
            .build();
    }

    @DisplayName("유저 회원가입을 한다.")
    @Test
    void createUser() throws Exception {
        AccountRequest accountRequest = AccountRequest.builder()
            .accountBank("국민은행")
            .accountNumber("123-1234-12345")
            .accountOwner("남상우")
            .build();

        SignUpRequest signUpRequest = SignUpRequest.builder()
            .identity("highright96")
            .password("1234")
            .name("남상우")
            .email("highright96@email.com")
            .accountRequest(accountRequest)
            .build();

        doNothing().when(userService).createUser(any());

        mockMvc.perform(post("/api/user/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest)))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(UserDocumentation.userSignup());
    }

    @WithMockCustomUser
    @DisplayName("내 정보를 조회한다.")
    @Test
    void getMyInfo() throws Exception {
        UserResponse userResponse = UserResponse.of(user, of(account));

        when(userService.getMyInfo(any())).thenReturn(userResponse);

        mockMvc.perform(get("/api/user/me"))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(UserDocumentation.userMe());
    }
}
