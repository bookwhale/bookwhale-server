package com.teamherb.bookstoreback.common.utils.bookapi;


import com.teamherb.bookstoreback.common.controller.CommonApiTest;
import com.teamherb.bookstoreback.common.utils.bookapi.service.BookApiServiceXml;
import com.teamherb.bookstoreback.post.controller.PostBookController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;



import static com.teamherb.bookstoreback.common.utils.bookapi.service.BookApiServiceXml.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("책 API 통합 테스트")
@WebMvcTest(controllers = PostBookController.class)
public class NaverBookApiTest extends CommonApiTest {

    @MockBean
    BookApiServiceXml bookapiServiceXml;

    @Test
    @DisplayName("책 API 단위 테스트")

    public void BookapiPostTest() throws Exception {
        Search search = Search.builder()
                .title("기억")
                .build();
        mockMvc.perform(post("/api/bookrequest")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(search)))
                .andExpect(status().isOk())
                .andDo(print());



    }

}
