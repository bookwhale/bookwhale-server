package com.teamherb.bookstoreback.post.controller;


import static java.lang.String.format;
import static java.util.List.of;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.teamherb.bookstoreback.common.controller.CommonApiTest;
import com.teamherb.bookstoreback.common.security.WithMockCustomUser;
import com.teamherb.bookstoreback.post.docs.PostDocumentation;
import com.teamherb.bookstoreback.post.dto.BookResponse;
import com.teamherb.bookstoreback.post.dto.NaverBookRequest;
import com.teamherb.bookstoreback.post.service.NaverBookAPIService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;

@WebMvcTest(controllers = PostController.class)
public class PostControllerTest extends CommonApiTest {

    @MockBean
    NaverBookAPIService naverBookAPIService;

    @Test
    @DisplayName("네이버 책 API")
    @WithMockCustomUser
    public void findNaverBooksTest() throws Exception {
        NaverBookRequest naverBookRequest = NaverBookRequest.builder()
            .title("책 제목")
            .build();

        BookResponse bookResponse = BookResponse.builder()
            .bookIsbn("12398128745902")
            .bookListPrice("10000")
            .bookThumbnail("썸네일")
            .bookTitle("책 제목")
            .bookPublisher("출판사")
            .bookAuthor("작가")
            .bookSummary("설명")
            .bookPubDate("2021-12-12")
            .build();

        when(naverBookAPIService.getNaverBooks(any())).thenReturn(of(bookResponse));

        mockMvc.perform(get(format("/api/post/naverBookAPI?title=%s", naverBookRequest.getTitle()))
                .header(HttpHeaders.AUTHORIZATION, "accessToken"))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(PostDocumentation.findNaverBooks());
    }
}
