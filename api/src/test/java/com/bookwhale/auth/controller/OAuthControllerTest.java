package com.bookwhale.auth.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bookwhale.auth.dto.OAuthRefreshLoginRequest;
import com.bookwhale.common.controller.CommonApiTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@DisplayName("사용자 인증 관련 단위 테스트(Controller)")
@WebMvcTest(controllers = OAuthController.class)
class OAuthControllerTest extends CommonApiTest {

    @Test
    @DisplayName("provider명으로 요청하면 provider의 로그인 URL로 redirect 된다. (naver, google)")
    void requestLoginForRedirectProviderLogin() throws Exception {
        mockMvc.perform(get("/api/oauth/{providerType}", "naver"))
            .andExpect(status().isOk())
            .andDo(print());
    }

    @Test
    @DisplayName("provider 에 로그인이 완료되면 전달받는 요청 키로 로그인 절차를 진행한다.")
    void oAuthLoginProcessAfterProviderLogin() throws Exception {
        mockMvc.perform(get("/api/oauth/{providerType}/login", "google")
                .param("code", "accessCode"))
            .andExpect(status().isOk())
            .andDo(print());
    }

    @Test
    @DisplayName("발급한 refreshToken을 확인하여 apiToken refresh 요청을 수행한다.")
    void refreshApiTokenWithRefreshToken() throws Exception {
        var refreshRequest = new OAuthRefreshLoginRequest("apiToken", "refreshToken");
        String requestBody = objectMapper.writeValueAsString(refreshRequest);

        mockMvc.perform(post("/api/oauth/refresh")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestBody))
            .andExpect(status().isOk())
            .andDo(print());
    }

    @Test
    @DisplayName("로그아웃 요청을 수행한다. (refreshToken 제거)")
    void logoutUser() throws Exception {
        var refreshRequest = new OAuthRefreshLoginRequest("apiToken", "refreshToken");
        String requestBody = objectMapper.writeValueAsString(refreshRequest);

        mockMvc.perform(post("/api/oauth/logout")
                .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestBody))
            .andExpect(status().isOk())
            .andDo(print());
    }

    @Test
    @DisplayName("회원 탈퇴 요청을 수행한다. (refreshToken 제거, User 제거)")
    void withdrawalUser() throws Exception {
        var refreshRequest = new OAuthRefreshLoginRequest("apiToken", "refreshToken");
        String requestBody = objectMapper.writeValueAsString(refreshRequest);

        mockMvc.perform(post("/api/oauth/withdrawal")
                .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestBody))
            .andExpect(status().isOk())
            .andDo(print());
    }

}