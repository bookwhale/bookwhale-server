package com.teamherb.bookstoreback.common.utils.bookapi;

import com.teamherb.bookstoreback.common.controller.CommonApiTest;
import com.teamherb.bookstoreback.common.utils.bookapi.service.BookApiServiceXml;
import com.teamherb.bookstoreback.post.controller.PostBookController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("책 API 테스트(Controller)")
@WebMvcTest(controllers = PostBookController.class)
public class NaverBookApiTest extends CommonApiTest {



    @MockBean
    private  BookApiServiceXml bookapiServiceXml;

    @BeforeEach
    @Override
    public void setUp(WebApplicationContext webApplicationContext) {
        super.setUp(webApplicationContext);
    }

    @Test
    
    public void BookapiPostTest() throws Exception {
        String Search = "{\"title\": \"기억\"}";

        mockMvc.perform(post("/api/bookrequest")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Search))
                .andExpect(status().isOk())
                .andDo(print());



    }

}
