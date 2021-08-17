package com.teamherb.bookstoreback.account.controller;

import static java.util.List.of;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.teamherb.bookstoreback.account.docs.AccountDocumentation;
import com.teamherb.bookstoreback.account.dto.AccountRequest;
import com.teamherb.bookstoreback.account.dto.AccountResponse;
import com.teamherb.bookstoreback.account.dto.AccountUpdateRequest;
import com.teamherb.bookstoreback.account.service.AccountService;
import com.teamherb.bookstoreback.common.controller.CommonApiTest;
import com.teamherb.bookstoreback.common.security.WithMockCustomUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

@DisplayName("계좌 단위 테스트(Controller")
@WebMvcTest(controllers = AccountController.class)
class AccountControllerTest extends CommonApiTest {

    @MockBean
    AccountService accountService;

    @WithMockCustomUser
    @DisplayName("계좌를 생성한다.")
    @Test
    void createAccount() throws Exception {
        AccountRequest accountRequest = AccountRequest.builder()
            .accountNumber("123-1234-12345")
            .accountOwner("남상우")
            .accountBank("국민은행")
            .build();

        doNothing().when(accountService).createAccount(any(), any());

        mockMvc.perform(post("/api/account")
                .header("jwt", "accessToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accountRequest)))
            .andExpect(header().string("location", "/api/account"))
            .andExpect(status().isCreated())
            .andDo(print())
            .andDo(AccountDocumentation.createAccount());
    }

    @WithMockCustomUser
    @DisplayName("계좌를 조회한다.")
    @Test
    void findAccounts() throws Exception {
        AccountResponse accountResponse = AccountResponse.builder()
            .accountNumber("123-1234-12345")
            .accountOwner("남상우")
            .accountBank("국민은행")
            .build();

        when(accountService.findAccounts(any())).thenReturn(of(accountResponse));

        mockMvc.perform(get("/api/account")
                .header("jwt", "accessToken"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].accountNumber").value(
                accountResponse.getAccountNumber()))
            .andExpect(jsonPath("$[0].accountBank").value(
                accountResponse.getAccountBank()))
            .andExpect(jsonPath("$[0].accountOwner").value(
                accountResponse.getAccountOwner()))
            .andDo(print())
            .andDo(AccountDocumentation.findAccounts());
    }

    @WithMockCustomUser
    @DisplayName("계좌를 수정한다.")
    @Test
    void updateAccount() throws Exception {
        AccountUpdateRequest req = AccountUpdateRequest.builder()
            .accountId(1L)
            .accountNumber("123-1234-12345")
            .accountOwner("남상우")
            .accountBank("국민은행")
            .build();

        doNothing().when(accountService).updateAccount(any(), any());

        mockMvc.perform(patch("/api/account")
                .header("jwt", "accessToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(AccountDocumentation.updateAccount());
    }

    @WithMockCustomUser
    @DisplayName("계좌를 삭제한다.")
    @Test
    void deleteAccount() throws Exception {
        doNothing().when(accountService).deleteAccount(any(), any());

        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/account/{accountId}", 1L)
                .header("jwt", "accessToken"))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(AccountDocumentation.deleteAccount());
    }
}