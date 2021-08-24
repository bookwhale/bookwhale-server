package com.teamherb.bookstoreback.post.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.teamherb.bookstoreback.account.dto.AccountRequest;
import com.teamherb.bookstoreback.account.dto.AccountResponse;
import com.teamherb.bookstoreback.common.controller.CommonApiTest;
import com.teamherb.bookstoreback.common.security.WithMockCustomUser;
import com.teamherb.bookstoreback.post.docs.PostDocumentation;
import com.teamherb.bookstoreback.post.domain.BookStatus;
import com.teamherb.bookstoreback.post.domain.PostStatus;
import com.teamherb.bookstoreback.post.dto.BookRequest;
import com.teamherb.bookstoreback.post.dto.BookResponse;
import com.teamherb.bookstoreback.post.dto.PostRequest;
import com.teamherb.bookstoreback.post.dto.PostResponse;
import com.teamherb.bookstoreback.post.service.PostService;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

@DisplayName("게시글 단위 테스트(Controller)")
@WebMvcTest(controllers = PostController.class)
public class PostControllerTest extends CommonApiTest {

    @MockBean
    PostService postService;

    @WithMockCustomUser
    @DisplayName("게시글을 등록한다.")
    @Test
    void createPost() throws Exception {
        MockMultipartFile image1 = new MockMultipartFile("images", "bookImage1.jpg",
            ContentType.IMAGE_JPEG.getMimeType(),
            "이미지1 입니다.".getBytes());

        MockMultipartFile image2 = new MockMultipartFile("images", "bookImage2.jpg",
            ContentType.IMAGE_JPEG.getMimeType(),
            "이미지2 입니다.".getBytes());

        AccountRequest accountRequest = AccountRequest.builder()
            .accountBank("국민은행")
            .accountOwner("남상우")
            .accountNumber("123-1234-12345")
            .build();

        BookRequest bookRequest = BookRequest.builder()
            .bookSummary("설명")
            .bookPubDate("2021-12-12")
            .bookIsbn("12398128745902")
            .bookListPrice("10000")
            .bookThumbnail("썸네일")
            .bookTitle("책 제목")
            .bookPublisher("출판사")
            .bookAuthor("작가")
            .build();

        PostRequest postRequest = PostRequest.builder()
            .accountRequest(accountRequest)
            .bookRequest(bookRequest)
            .title("책 팝니다~")
            .description("쿨 거래시 1000원 할인해드려요~")
            .bookStatus("BEST")
            .price("5000")
            .build();

        String content = objectMapper.writeValueAsString(postRequest);
        MockMultipartFile json = new MockMultipartFile("postRequest", "jsondata",
            "application/json", content.getBytes(StandardCharsets.UTF_8));

        when(postService.createPost(any(), any(), any())).thenReturn(1L);

        mockMvc.perform(multipart("/api/post")
                .file(image1)
                .file(image2)
                .file(json)
                .header(HttpHeaders.AUTHORIZATION, "accessToken")
                .contentType(MediaType.MULTIPART_MIXED))
            .andExpect(header().string("location", "/api/post/1"))
            .andExpect(status().isCreated())
            .andDo(print())
            .andDo(PostDocumentation.createPost());
    }

    @WithMockCustomUser
    @DisplayName("게시글을 상세 조회한다.")
    @Test
    void findPost() throws Exception {
        AccountResponse accountResponse = AccountResponse.builder()
            .accountBank("국민은행")
            .accountOwner("남상우")
            .accountNumber("123-1234-12345")
            .build();

        BookResponse bookResponse = BookResponse.builder()
            .bookSummary("설명")
            .bookPubDate("2021-12-12")
            .bookIsbn("12398128745902")
            .bookListPrice("10000")
            .bookThumbnail("썸네일")
            .bookTitle("책 제목")
            .bookPublisher("출판사")
            .bookAuthor("작가")
            .build();

        PostResponse postResponse = PostResponse.builder()
            .accountResponse(accountResponse)
            .bookResponse(bookResponse)
            .postId(1L)
            .title("책 팝니다~")
            .price("5000")
            .description("쿨 거래시 1000원 할인해드려요~")
            .bookStatus(BookStatus.BEST)
            .postStatus(PostStatus.SALE)
            .images(List.of("image1", "image2"))
            .isMyPost(true)
            .build();

        when(postService.findPost(any(), any())).thenReturn(postResponse);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/post/{postId}", 1L)
                .header(HttpHeaders.AUTHORIZATION, "accessToken"))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(PostDocumentation.findPost());
    }
}