package com.bookwhale.article.controller;

import static java.lang.String.format;
import static java.util.List.of;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bookwhale.article.docs.ArticleDocumentation;
import com.bookwhale.article.domain.ArticleStatus;
import com.bookwhale.article.domain.BookStatus;
import com.bookwhale.article.dto.ArticleRequest;
import com.bookwhale.article.dto.ArticleResponse;
import com.bookwhale.article.dto.ArticleStatusUpdateRequest;
import com.bookwhale.article.dto.ArticleUpdateRequest;
import com.bookwhale.article.dto.ArticlesRequest;
import com.bookwhale.article.dto.ArticlesResponse;
import com.bookwhale.article.dto.BookRequest;
import com.bookwhale.article.dto.BookResponse;
import com.bookwhale.article.dto.NaverBookRequest;
import com.bookwhale.article.service.ArticleService;
import com.bookwhale.article.service.NaverBookAPIService;
import com.bookwhale.common.controller.CommonApiTest;
import com.bookwhale.common.domain.Location;
import com.bookwhale.common.dto.Pagination;
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
import org.springframework.restdocs.payload.JsonFieldType;

@DisplayName("????????? ?????? ?????????(Controller)")
@WebMvcTest(controllers = ArticleController.class)
public class ArticleControllerTest extends CommonApiTest {

    @MockBean
    ArticleService articleService;

    @MockBean
    NaverBookAPIService naverBookAPIService;

    @Test
    @DisplayName("????????? ??? API")
    public void findNaverBooksTest() throws Exception {
        NaverBookRequest request = NaverBookRequest.builder()
            .title("??? ??????")
            .display(10)
            .start(1)
            .build();

        BookResponse bookResponse = BookResponse.builder()
            .bookIsbn("12398128745902")
            .bookListPrice("10000")
            .bookThumbnail("?????????")
            .bookTitle("??? ??????")
            .bookPublisher("?????????")
            .bookAuthor("??????")
            .bookSummary("??????")
            .bookPubDate("2021-12-12")
            .build();

        when(naverBookAPIService.getNaverBooks(any())).thenReturn(of(bookResponse));

        mockMvc.perform(get(format("/api/article/naver-book?title=%s&display=%d&start=%d",
                request.getTitle(), request.getDisplay(), request.getStart()))
                .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken"))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(ArticleDocumentation.findNaverBooks());
    }


    @DisplayName("???????????? ????????????.")
    @Test
    void createArticle() throws Exception {
        MockMultipartFile image1 = new MockMultipartFile("images", "bookImage1.jpg",
            ContentType.IMAGE_JPEG.getMimeType(),
            "?????????1 ?????????.".getBytes());

        MockMultipartFile image2 = new MockMultipartFile("images", "bookImage2.jpg",
            ContentType.IMAGE_JPEG.getMimeType(),
            "?????????2 ?????????.".getBytes());

        BookRequest bookRequest = BookRequest.builder()
            .bookSummary("??????")
            .bookPubDate("2021-12-12")
            .bookIsbn("12398128745902")
            .bookListPrice("10000")
            .bookThumbnail("?????????")
            .bookTitle("??? ??????")
            .bookPublisher("?????????")
            .bookAuthor("??????")
            .build();

        ArticleRequest articleRequest = ArticleRequest.builder()
            .bookRequest(bookRequest)
            .title("??? ?????????~")
            .description("??? ????????? 1000??? ??????????????????~")
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
                .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
                .contentType(MediaType.MULTIPART_MIXED))
            .andExpect(header().string("location", "/api/article/1"))
            .andExpect(status().isCreated())
            .andDo(print())
            .andDo(ArticleDocumentation.createArticle());
    }


    @DisplayName("???????????? ????????? ??? ???????????? ????????? ????????? ????????? ????????????.")
    @Test
    void createArticle_notExistRequestPart_failure() throws Exception {
        BookRequest bookRequest = BookRequest.builder()
            .bookSummary("??????")
            .bookPubDate("2021-12-12")
            .bookIsbn("12398128745902")
            .bookListPrice("10000")
            .bookThumbnail("?????????")
            .bookTitle("??? ??????")
            .bookPublisher("?????????")
            .bookAuthor("??????")
            .build();

        ArticleRequest articleRequest = ArticleRequest.builder()
            .bookRequest(bookRequest)
            .title("??? ?????????~")
            .description("??? ????????? 1000??? ??????????????????~")
            .bookStatus("BEST")
            .price("5000")
            .build();

        String content = objectMapper.writeValueAsString(articleRequest);
        MockMultipartFile json = new MockMultipartFile("articleRequest", "jsonData",
            "application/json", content.getBytes(StandardCharsets.UTF_8));

        when(articleService.createArticle(any(), any(), any())).thenReturn(1L);

        mockMvc.perform(multipart("/api/article")
                .file(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
                .contentType(MediaType.MULTIPART_MIXED))
            .andExpect(status().isBadRequest())
            .andDo(print());
    }


    @DisplayName("???????????? ?????? ????????????.")
    @Test
    void findArticle() throws Exception {
        BookResponse bookResponse = BookResponse.builder()
            .bookSummary("??????")
            .bookPubDate("2021-12-12")
            .bookIsbn("12398128745902")
            .bookListPrice("10000")
            .bookThumbnail("?????????")
            .bookTitle("????????? ?????????")
            .bookPublisher("?????????")
            .bookAuthor("?????????")
            .build();

        ArticleResponse articleResponse = ArticleResponse.builder()
            .sellerId(1L)
            .sellerIdentity("sellerIdentity")
            .sellerProfileImage("sellerProfileImage")
            .articleId(1L)
            .title("??? ?????????~")
            .price("5000???")
            .description("??? ????????? 1000??? ??????????????????~")
            .bookStatus(BookStatus.BEST.getName())
            .articleStatus(ArticleStatus.SALE.getName())
            .images(of("image1", "image2"))
            .bookResponse(bookResponse)
            .isMyArticle(true)
            .isMyFavorite(true)
            .myFavoriteId(1L)
            .sellingLocation("??????")
            .viewCount(1L)
            .favoriteCount(0L)
            .beforeTime("15??? ???")
            .build();

        when(articleService.findArticle(any(), any())).thenReturn(articleResponse);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/article/{articleId}", 1L)
                .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken"))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(ArticleDocumentation.findArticle());
    }


    @DisplayName("??? ??????????????? ????????????.")
    @Test
    void findMyArticles() throws Exception {
        ArticlesResponse articlesResponse = ArticlesResponse.builder()
            .articleId(1L)
            .articleImage("?????????")
            .articleTitle("??? ?????????~")
            .articlePrice("20000???")
            .bookStatus(BookStatus.BEST.getName())
            .sellingLocation("??????")
            .chatCount(1L)
            .favoriteCount(1L)
            .beforeTime("15??? ???")
            .build();

        when(articleService.findMyArticles(any())).thenReturn(List.of(articlesResponse));

        mockMvc.perform(get("/api/articles/me")
                .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken"))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(ArticleDocumentation.findMyArticles());
    }


    @DisplayName("???????????? ????????? ???????????? ?????? ????????????.")
    @Test
    void findArticles_loginUser() throws Exception {
        Pagination pagination = new Pagination(0, 10);

        ArticlesRequest articlesRequest = ArticlesRequest.builder()
            .search("??? ??????")
            .build();

        ArticlesResponse articlesResponse = ArticlesResponse.builder()
            .articleId(1L)
            .articleImage("?????????")
            .articleTitle("??? ?????????~")
            .articlePrice("20000???")
            .bookStatus(BookStatus.BEST.getName())
            .sellingLocation(Location.SEOUL.getName())
            .chatCount(1L)
            .favoriteCount(0L)
            .beforeTime("15??? ???")
            .build();

        when(articleService.findArticles(any(), any())).thenReturn(of(articlesResponse));

        mockMvc.perform(
                get(format("/api/articles?search=%s&page=%d&size=%d", articlesRequest.getSearch(),
                    pagination.getPage(), pagination.getSize())))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(ArticleDocumentation.findArticles());
    }

    @DisplayName("??????????????? ?????? ????????? ???????????? ?????? ????????????.")
    @Test
    void findArticles_anonymousUser() throws Exception {
        Pagination pagination = new Pagination(0, 10);

        ArticlesRequest articlesRequest = ArticlesRequest.builder()
            .search("??? ??????")
            .build();

        ArticlesResponse articlesResponse = ArticlesResponse.builder()
            .articleId(1L)
            .articleImage("?????????")
            .articleTitle("??? ?????????~")
            .articlePrice("20000???")
            .bookStatus(BookStatus.BEST.getName())
            .sellingLocation(Location.SEOUL.getName())
            .chatCount(1L)
            .favoriteCount(0L)
            .beforeTime("15??? ???")
            .build();

        when(articleService.findArticles(any(), any())).thenReturn(of(articlesResponse));

        mockMvc.perform(
                get(format("/api/articles?search=%s&page=%d&size=%d", articlesRequest.getSearch(),
                    pagination.getPage(), pagination.getSize())))
            .andExpect(status().isOk())
            .andDo(print());
    }


    @DisplayName("???????????? ????????????.")
    @Test
    void updateArticle_success() throws Exception {
        MockMultipartFile image1 = new MockMultipartFile("images", "updateImage.jpg",
            ContentType.IMAGE_JPEG.getMimeType(),
            "??????????????????.".getBytes());

        MockMultipartFile image2 = new MockMultipartFile("images", "updateImage.jpg",
            ContentType.IMAGE_JPEG.getMimeType(),
            "??????????????????.".getBytes());

        ArticleUpdateRequest request = ArticleUpdateRequest.builder()
            .title("??? ?????????~ (?????? ??????)")
            .description("??? ????????? 1000??? ??????????????????~ (?????? ??????)")
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
                .file(image1)
                .file(image2)
                .file(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
                .contentType(MediaType.MULTIPART_MIXED))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(ArticleDocumentation.updateArticle());
    }

    @DisplayName("????????? ????????? ????????????.")
    @Test
    void updateArticleStatus() throws Exception {
        ArticleStatusUpdateRequest request = new ArticleStatusUpdateRequest(
            ArticleStatus.SOLD_OUT.toString());

        mockMvc.perform(
                RestDocumentationRequestBuilders.patch("/api/article/{articleId}/status", 1L)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(ArticleDocumentation.updateArticleStatus());
    }

    @DisplayName("???????????? ????????????.")
    @Test
    void deleteArticle() throws Exception {
        mockMvc.perform(
                RestDocumentationRequestBuilders.delete("/api/article/{articleId}", 1L)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(ArticleDocumentation.deleteArticle());
    }


    @DisplayName("??????????????? ????????? ????????? ?????? ????????? ????????????.")
    @Test
    void getSearchConditionsOfAllBookStatus() throws Exception {
        String kindOfCondition = "bookStatus";
        mockMvc.perform(get("/api/article/conditions/" + kindOfCondition))
            .andExpect(status().isOk())
            .andDo(ArticleDocumentation.getSearchConditions(kindOfCondition))
            .andDo(print());
    }

    @DisplayName("??????????????? ????????? ???????????? ????????? ????????????.")
    @Test
    void getSearchConditionsOfAllSellingLocation() throws Exception {
        String kindOfCondition = "locations";
        mockMvc.perform(get("/api/article/conditions/" + kindOfCondition))
            .andExpect(status().isOk())
            .andDo(ArticleDocumentation.getSearchConditions(kindOfCondition))
            .andDo(print());
    }
}
