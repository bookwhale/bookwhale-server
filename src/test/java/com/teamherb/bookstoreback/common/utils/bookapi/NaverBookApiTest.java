package com.teamherb.bookstoreback.common.utils.bookapi;

import com.teamherb.bookstoreback.common.utils.bookapi.service.BookapiServiceXml;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("NAVER 책 api post 통합 테스트(xml)")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class NaverBookApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookapiServiceXml bookapiServiceXml;

    @Test
    @DisplayName("Api 호출 정보를 가져온다.")
    public void BookapiPostTest() throws Exception {
        String Search = "{\"title\": \"기억\"}";

        mockMvc.perform(post("/requestbookapi")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .contentType(MediaType.APPLICATION_JSON)
                .content(Search))
                .andExpect(status().isOk())
                .andDo(print());



    }

}
