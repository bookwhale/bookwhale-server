package com.teamherb.bookstoreback.post.controller;


import com.teamherb.bookstoreback.common.controller.CommonApiTest;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

@WebMvcTest(controllers = PostController.class)
public class PostControllerTest extends CommonApiTest {

    /*
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
     */
}
