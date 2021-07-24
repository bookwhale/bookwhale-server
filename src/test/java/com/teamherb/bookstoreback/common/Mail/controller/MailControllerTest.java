package com.teamherb.bookstoreback.common.Mail.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class MailControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void 메일테스트_Post() throws Exception {

        String content = "{\"address\": \"hose0728@naver.com\", \"title\": \"MockMVC테스트\",\"message\": \"팀허브 테스트\"}";
        mockMvc.perform(post("/mail")
                .contentType(MediaType.APPLICATION_JSON) // json 형식으로 받을 것을 설정
                .content(content)) // http body 내용
                .andExpect(status().isOk()) // 호출하였을 때 보낼 http 응답
                .andExpect(content().string("ok")) // 응답 내용 검증
                .andDo(print()); // 응답내용 출력

    }








}