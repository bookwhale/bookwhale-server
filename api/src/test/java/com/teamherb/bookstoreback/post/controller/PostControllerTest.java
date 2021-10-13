package com.teamherb.bookstoreback.post.controller;

import static java.lang.String.format;
import static java.util.List.of;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.teamherb.bookstoreback.common.controller.CommonApiTest;
import com.teamherb.bookstoreback.common.security.WithMockCustomUser;
import com.teamherb.bookstoreback.dto.Pagination;
import com.teamherb.bookstoreback.post.docs.PostDocumentation;
import com.teamherb.bookstoreback.post.domain.BookStatus;
import com.teamherb.bookstoreback.post.domain.PostStatus;
import com.teamherb.bookstoreback.post.dto.BookRequest;
import com.teamherb.bookstoreback.post.dto.BookResponse;
import com.teamherb.bookstoreback.post.dto.NaverBookRequest;
import com.teamherb.bookstoreback.post.dto.PostRequest;
import com.teamherb.bookstoreback.post.dto.PostResponse;
import com.teamherb.bookstoreback.post.dto.PostStatusUpdateRequest;
import com.teamherb.bookstoreback.post.dto.PostUpdateRequest;
import com.teamherb.bookstoreback.post.dto.PostsRequest;
import com.teamherb.bookstoreback.post.dto.PostsResponse;
import com.teamherb.bookstoreback.post.service.NaverBookAPIService;
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

  @MockBean
  NaverBookAPIService naverBookAPIService;

  @Test
  @DisplayName("네이버 책 API")
  @WithMockCustomUser
  public void findNaverBooksTest() throws Exception {
    NaverBookRequest request = NaverBookRequest.builder()
        .title("책 제목")
        .display(10)
        .start(1)
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

    mockMvc.perform(get(format("/api/post/naverBookAPI?title=%s&display=%d&start=%d",
            request.getTitle(), request.getDisplay(), request.getStart()))
            .header(HttpHeaders.AUTHORIZATION, "accessToken"))
        .andExpect(status().isOk())
        .andDo(print())
        .andDo(PostDocumentation.findNaverBooks());
  }

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
        .bookRequest(bookRequest)
        .title("책 팝니다~")
        .description("쿨 거래시 1000원 할인해드려요~")
        .bookStatus("BEST")
        .price("5000")
        .build();

    String content = objectMapper.writeValueAsString(postRequest);
    MockMultipartFile json = new MockMultipartFile("postRequest", "jsonData",
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
  @DisplayName("게시글을 등록할 때 이미지를 보내지 않으면 예외가 발생한다.")
  @Test
  void createPost_notExistRequestPart_failure() throws Exception {
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
        .bookRequest(bookRequest)
        .title("책 팝니다~")
        .description("쿨 거래시 1000원 할인해드려요~")
        .bookStatus("BEST")
        .price("5000")
        .build();

    String content = objectMapper.writeValueAsString(postRequest);
    MockMultipartFile json = new MockMultipartFile("postRequest", "jsonData",
        "application/json", content.getBytes(StandardCharsets.UTF_8));

    when(postService.createPost(any(), any(), any())).thenReturn(1L);

    mockMvc.perform(multipart("/api/post")
            .file(json)
            .header(HttpHeaders.AUTHORIZATION, "accessToken")
            .contentType(MediaType.MULTIPART_MIXED))
        .andExpect(status().isBadRequest())
        .andDo(print());
  }

  @WithMockCustomUser
  @DisplayName("게시글을 상세 조회한다.")
  @Test
  void findPost() throws Exception {
    BookResponse bookResponse = BookResponse.builder()
        .bookSummary("설명")
        .bookPubDate("2021-12-12")
        .bookIsbn("12398128745902")
        .bookListPrice("10000")
        .bookThumbnail("썸네일")
        .bookTitle("토비의 스프링")
        .bookPublisher("출판사")
        .bookAuthor("이일민")
        .build();

    PostResponse postResponse = PostResponse.builder()
        .sellerId(1L)
        .sellerIdentity("sellerIdentity")
        .sellerProfileImage("sellerProfileImage")
        .postId(1L)
        .title("책 팝니다~")
        .price("5000원")
        .description("쿨 거래시 1000원 할인해드려요~")
        .bookStatus(BookStatus.BEST.getName())
        .postStatus(PostStatus.SALE.getName())
        .images(of("image1", "image2"))
        .bookResponse(bookResponse)
        .isMyPost(true)
        .isMyInterest(true)
        .beforeTime("15분 전")
        .build();

    when(postService.findPost(any(), any())).thenReturn(postResponse);

    mockMvc.perform(RestDocumentationRequestBuilders.get("/api/post/{postId}", 1L)
            .header(HttpHeaders.AUTHORIZATION, "accessToken"))
        .andExpect(status().isOk())
        .andDo(print())
        .andDo(PostDocumentation.findPost());
  }

  @WithMockCustomUser
  @DisplayName("로그인한 유저가 게시글을 전체 조회한다.")
  @Test
  void findPosts_loginUser() throws Exception {
    Pagination pagination = new Pagination(0, 10);

    PostsRequest postsRequest = PostsRequest.builder()
        .title("책 제목")
        .build();

    PostsResponse postsResponse = PostsResponse.builder()
        .postId(1L)
        .postImage("이미지")
        .postTitle("책 팝니다~")
        .postPrice("20000원")
        .postStatus(PostStatus.SALE.getName())
        .bookTitle("토비의 스프링")
        .bookAuthor("이일민")
        .bookPublisher("허브출판사")
        .beforeTime("15분 전")
        .build();

    when(postService.findPosts(any(), any())).thenReturn(of(postsResponse));

    mockMvc.perform(get(format("/api/post?title=%s&page=%d&size=%d", postsRequest.getTitle(),
            pagination.getPage(), pagination.getSize()))
            .header(HttpHeaders.AUTHORIZATION, "accessToken"))
        .andExpect(status().isOk())
        .andDo(print())
        .andDo(PostDocumentation.findPosts());
  }

  @DisplayName("로그인하지 않은 유저가 게시글을 전체 조회한다.")
  @Test
  void findPosts_anonymousUser() throws Exception {
    Pagination pagination = new Pagination(0, 10);

    PostsRequest postsRequest = PostsRequest.builder()
        .title("책 제목")
        .build();

    PostsResponse postsResponse = PostsResponse.builder()
        .postId(1L)
        .postImage("이미지")
        .postTitle("책 팝니다~")
        .postPrice("20000원")
        .postStatus(PostStatus.SALE.getName())
        .bookTitle("토비의 스프링")
        .bookAuthor("이일민")
        .bookPublisher("허브출판사")
        .beforeTime("15분 전")
        .build();

    when(postService.findPosts(any(), any())).thenReturn(of(postsResponse));

    mockMvc.perform(get(format("/api/post?title=%s&page=%d&size=%d", postsRequest.getTitle(),
            pagination.getPage(), pagination.getSize())))
        .andExpect(status().isOk())
        .andDo(print());
  }

  @WithMockCustomUser
  @DisplayName("게시글을 수정한다.")
  @Test
  void updatePost_success() throws Exception {
    MockMultipartFile updateImage = new MockMultipartFile("images", "updateImage.jpg",
        ContentType.IMAGE_JPEG.getMimeType(),
        "수정된 이미지입니다.".getBytes());

    PostUpdateRequest request = PostUpdateRequest.builder()
        .title("책 팝니다~ (가격 내림)")
        .description("쿨 거래시 1000원 할인해드려요~ (가격 내림)")
        .bookStatus("LOWER")
        .price("2000")
        .deleteImgUrls(List.of("image1", "image2"))
        .build();

    String content = objectMapper.writeValueAsString(request);
    MockMultipartFile json = new MockMultipartFile("postUpdateRequest", "jsonData",
        "application/json", content.getBytes(StandardCharsets.UTF_8));

    doNothing().when(postService).updatePost(any(), any(), any(), any());

    mockMvc.perform(MockMultipartPatchBuilder("/api/post/1")
            .file(updateImage)
            .file(json)
            .header(HttpHeaders.AUTHORIZATION, "accessToken")
            .contentType(MediaType.MULTIPART_MIXED))
        .andExpect(status().isOk())
        .andDo(print())
        .andDo(PostDocumentation.updatePost());
  }

  @WithMockCustomUser
  @DisplayName("게시글 상태를 변경한다.")
  @Test
  void updatePostStatus() throws Exception {
    PostStatusUpdateRequest request = new PostStatusUpdateRequest(PostStatus.SOLD_OUT.toString());

    mockMvc.perform(RestDocumentationRequestBuilders.patch("/api/post/postStatus/{postId}", 1L)
            .header(HttpHeaders.AUTHORIZATION, "accessToken")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andDo(print())
        .andDo(PostDocumentation.updatePostStatus());
  }
}