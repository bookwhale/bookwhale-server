package com.teamherb.bookstoreback.post.controller;


import com.teamherb.bookstoreback.common.controller.CommonApiTest;
import com.teamherb.bookstoreback.common.security.WithMockCustomUser;
import com.teamherb.bookstoreback.common.utils.bookapi.service.BookApiXmlService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;



import static com.teamherb.bookstoreback.common.utils.bookapi.service.BookApiXmlService.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("책 API 통합 테스트")
@WebMvcTest(controllers = PostController.class)
public class PostControllerTest extends CommonApiTest {

    @MockBean
    BookApiXmlService bookapiXmlService;

    @Test
    @DisplayName("책 API 단위 테스트")
    @WithMockCustomUser
    public void BookapiPostTest() throws Exception {
        Search search = Search.builder()
                .title("기억")
                .build();
        String s = bookapiXmlService.BookApixmlRequest(search);
        System.out.println("s = " + s);
        mockMvc.perform(post("/api/bookrequest")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(search)))
                .andExpect(status().isOk())
                .andDo(print());



    }

}
