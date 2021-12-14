package com.bookwhale.article.controller;

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

import com.bookwhale.article.domain.ArticleStatus;
import com.bookwhale.article.dto.ArticleRequest;
import com.bookwhale.article.dto.ArticlesResponse;
import com.bookwhale.common.controller.CommonApiTest;
import com.bookwhale.common.domain.Location;
import com.bookwhale.common.security.WithMockCustomUser;
import com.bookwhale.common.dto.Pagination;
import com.bookwhale.article.docs.ArticleDocumentation;
import com.bookwhale.article.domain.BookStatus;
import com.bookwhale.article.dto.BookRequest;
import com.bookwhale.article.dto.BookResponse;
import com.bookwhale.article.dto.NaverBookRequest;
import com.bookwhale.article.dto.ArticleResponse;
import com.bookwhale.article.dto.ArticleStatusUpdateRequest;
import com.bookwhale.article.dto.ArticleUpdateRequest;
import com.bookwhale.article.dto.ArticlesRequest;
import com.bookwhale.article.service.NaverBookAPIService;
import com.bookwhale.article.service.ArticleService;
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
@WebMvcTest(controllers = ArticleController.class)
public class ArticleControllerTest extends CommonApiTest {

    @MockBean
    ArticleService articleService;

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

        mockMvc.perform(get(format("/api/article/naverBookAPI?title=%s&display=%d&start=%d",
                request.getTitle(), request.getDisplay(), request.getStart()))
                .header(HttpHeaders.AUTHORIZATION, "accessToken"))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(ArticleDocumentation.findNaverBooks());
    }

    @WithMockCustomUser
    @DisplayName("게시글을 등록한다.")
    @Test
    void createArticle() throws Exception {
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

        ArticleRequest articleRequest = ArticleRequest.builder()
            .bookRequest(bookRequest)
            .title("책 팝니다~")
            .description("쿨 거래시 1000원 할인해드려요~")
            .bookStatus("BEST")
            .price("5000")
            .sellingLocation("BUSAN")
            .build();

        String content = objectMapper.writeValueAsString(articleRequest);
        MockMultipartFile json = new MockMultipartFile("articleRequest", "jsonData",
            "application/json", content.getBytes(StandardCharsets.UTF_8));

        when(articleService.createArticle(any(), any(), any())).thenReturn(1L);

        mockMvc.perform(multipart("/api/article")
                .file(image1)
                .file(image2)
                .file(json)
                .header(HttpHeaders.AUTHORIZATION, "accessToken")
                .contentType(MediaType.MULTIPART_MIXED))
            .andExpect(header().string("location", "/api/article/1"))
            .andExpect(status().isCreated())
            .andDo(print())
            .andDo(ArticleDocumentation.createArticle());
    }

    @WithMockCustomUser
    @DisplayName("게시글을 등록할 때 이미지를 보내지 않으면 예외가 발생한다.")
    @Test
    void createArticle_notExistRequestPart_failure() throws Exception {
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

        ArticleRequest articleRequest = ArticleRequest.builder()
            .bookRequest(bookRequest)
            .title("책 팝니다~")
            .description("쿨 거래시 1000원 할인해드려요~")
            .bookStatus("BEST")
            .price("5000")
            .build();

        String content = objectMapper.writeValueAsString(articleRequest);
        MockMultipartFile json = new MockMultipartFile("articleRequest", "jsonData",
            "application/json", content.getBytes(StandardCharsets.UTF_8));

        when(articleService.createArticle(any(), any(), any())).thenReturn(1L);

        mockMvc.perform(multipart("/api/article")
                .file(json)
                .header(HttpHeaders.AUTHORIZATION, "accessToken")
                .contentType(MediaType.MULTIPART_MIXED))
            .andExpect(status().isBadRequest())
            .andDo(print());
    }

    @WithMockCustomUser
    @DisplayName("게시글을 상세 조회한다.")
    @Test
    void findArticle() throws Exception {
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

        ArticleResponse articleResponse = ArticleResponse.builder()
            .sellerId(1L)
            .sellerIdentity("sellerIdentity")
            .sellerProfileImage("sellerProfileImage")
            .articleId(1L)
            .title("책 팝니다~")
            .price("5000원")
            .description("쿨 거래시 1000원 할인해드려요~")
            .bookStatus(BookStatus.BEST.getName())
            .articleStatus(ArticleStatus.SALE.getName())
            .images(of("image1", "image2"))
            .bookResponse(bookResponse)
            .isMyArticle(true)
            .isMyFavorite(true)
            .sellingLocation("서울")
            .viewCount(1L)
            .favoriteCount(0L)
            .beforeTime("15분 전")
            .build();

        when(articleService.findArticle(any(), any())).thenReturn(articleResponse);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/article/{articleId}", 1L)
                .header(HttpHeaders.AUTHORIZATION, "accessToken"))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(ArticleDocumentation.findArticle());
    }

    @WithMockCustomUser
    @DisplayName("로그인한 유저가 게시글을 전체 조회한다.")
    @Test
    void findArticles_loginUser() throws Exception {
        Pagination pagination = new Pagination(0, 10);

        ArticlesRequest articlesRequest = ArticlesRequest.builder()
            .title("책 제목")
            .build();

        ArticlesResponse articlesResponse = ArticlesResponse.builder()
            .articleId(1L)
            .articleImage("이미지")
            .articleTitle("책 팝니다~")
            .articlePrice("20000원")
            .articleStatus(ArticleStatus.SALE.getName())
            .description("판매자가 작성한 게시글 설명")
            .sellingLocation(Location.SEOUL.getName())
            .viewCount(1L)
            .favoriteCount(0L)
            .beforeTime("15분 전")
            .build();

        when(articleService.findArticles(any(), any())).thenReturn(of(articlesResponse));

        mockMvc.perform(get(format("/api/article?title=%s&page=%d&size=%d", articlesRequest.getTitle(),
                pagination.getPage(), pagination.getSize()))
                .header(HttpHeaders.AUTHORIZATION, "accessToken"))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(ArticleDocumentation.findArticles());
    }

    @DisplayName("로그인하지 않은 유저가 게시글을 전체 조회한다.")
    @Test
    void findArticles_anonymousUser() throws Exception {
        Pagination pagination = new Pagination(0, 10);

        ArticlesRequest articlesRequest = ArticlesRequest.builder()
            .title("책 제목")
            .build();

        ArticlesResponse articlesResponse = ArticlesResponse.builder()
            .articleId(1L)
            .articleImage("이미지")
            .articleTitle("책 팝니다~")
            .articlePrice("20000원")
            .articleStatus(ArticleStatus.SALE.getName())
            .beforeTime("15분 전")
            .build();

        when(articleService.findArticles(any(), any())).thenReturn(of(articlesResponse));

        mockMvc.perform(get(format("/api/article?title=%s&page=%d&size=%d", articlesRequest.getTitle(),
                pagination.getPage(), pagination.getSize())))
            .andExpect(status().isOk())
            .andDo(print());
    }

    @WithMockCustomUser
    @DisplayName("게시글을 수정한다.")
    @Test
    void updateArticle_success() throws Exception {
        MockMultipartFile updateImage = new MockMultipartFile("images", "updateImage.jpg",
            ContentType.IMAGE_JPEG.getMimeType(),
            "수정된 이미지입니다.".getBytes());

        ArticleUpdateRequest request = ArticleUpdateRequest.builder()
            .title("책 팝니다~ (가격 내림)")
            .description("쿨 거래시 1000원 할인해드려요~ (가격 내림)")
            .bookStatus("LOWER")
            .price("2000")
            .sellingLocation("SEOUL")
            .deleteImgUrls(List.of("image1", "image2"))
            .build();

        String content = objectMapper.writeValueAsString(request);
        MockMultipartFile json = new MockMultipartFile("articleUpdateRequest", "jsonData",
            "application/json", content.getBytes(StandardCharsets.UTF_8));

        doNothing().when(articleService).updateArticle(any(), any(), any(), any());

        mockMvc.perform(MockMultipartPatchBuilder("/api/article/1")
                .file(updateImage)
                .file(json)
                .header(HttpHeaders.AUTHORIZATION, "accessToken")
                .contentType(MediaType.MULTIPART_MIXED))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(ArticleDocumentation.updateArticle());
    }

    @WithMockCustomUser
    @DisplayName("게시글 상태를 변경한다.")
    @Test
    void updateArticleStatus() throws Exception {
        ArticleStatusUpdateRequest request = new ArticleStatusUpdateRequest(
            ArticleStatus.SOLD_OUT.toString());

        mockMvc.perform(RestDocumentationRequestBuilders.patch("/api/article/articleStatus/{articleId}", 1L)
                .header(HttpHeaders.AUTHORIZATION, "accessToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(ArticleDocumentation.updateArticleStatus());
    }

    @DisplayName("검색조건에 사용될 판매글 상태 목록을 조회한다.")
    @Test
    void getSearchConditionsOfAllBookStatus() throws Exception {
        String kindOfCondition = "bookStatus";
        mockMvc.perform(get("/api/article/conditions/" + kindOfCondition))
            .andExpect(status().isOk())
            .andDo(ArticleDocumentation.getSearchConditions(kindOfCondition))
            .andDo(print());
    }

    @DisplayName("검색조건에 사용될 판매지역 목록을 조회한다.")
    @Test
    void getSearchConditionsOfAllSellingLocation() throws Exception {
        String kindOfCondition = "locations";
        mockMvc.perform(get("/api/article/conditions/" + kindOfCondition))
            .andExpect(status().isOk())
            .andDo(ArticleDocumentation.getSearchConditions(kindOfCondition))
            .andDo(print());
    }
}