package com.teamherb.bookstoreback.user.controller;

import com.teamherb.bookstoreback.account.dto.AccountRequest;
import com.teamherb.bookstoreback.common.controller.CommonApiTest;
import com.teamherb.bookstoreback.user.dto.SignUpRequest;
import com.teamherb.bookstoreback.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("유저 단위 테스트(Controller)")
@WebMvcTest(controllers = UserController.class)
public class UserControllerTest extends CommonApiTest {

    @MockBean
    UserService userService;

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

        when(userService.createUser(any())).thenReturn(1L);

        mockMvc.perform(post("/api/user/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isCreated())
                .andDo(print())
                .andDo(document("user/signup",
                        requestFields(
                                fieldWithPath("identity").description("아이디"),
                                fieldWithPath("password").description("비밀번호"),
                                fieldWithPath("name").description("이름"),
                                fieldWithPath("email").description("이메일"),
                                fieldWithPath("accountRequest.accountNumber").description("계좌번호"),
                                fieldWithPath("accountRequest.accountBank").description("은행"),
                                fieldWithPath("accountRequest.accountOwner").description("이름")
                        )
                ));
    }
}
