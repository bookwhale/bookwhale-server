package com.bookwhale.article.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import com.bookwhale.article.domain.ArticleStatus;
import com.bookwhale.common.acceptance.AcceptanceTest;
import com.bookwhale.common.acceptance.AcceptanceUtils;
import com.bookwhale.common.acceptance.step.AcceptanceStep;
import com.bookwhale.dto.Pagination;
import com.bookwhale.article.acceptance.step.ArticleAcceptanceStep;
import com.bookwhale.article.domain.BookStatus;
import com.bookwhale.article.dto.BookRequest;
import com.bookwhale.article.dto.BookResponse;
import com.bookwhale.article.dto.NaverBookRequest;
import com.bookwhale.article.dto.ArticleRequest;
import com.bookwhale.article.dto.ArticleResponse;
import com.bookwhale.article.dto.ArticleStatusUpdateRequest;
import com.bookwhale.article.dto.ArticleUpdateRequest;
import com.bookwhale.article.dto.ArticlesRequest;
import com.bookwhale.article.dto.ArticlesResponse;
import com.bookwhale.user.acceptance.step.UserAcceptanceStep;
import com.bookwhale.user.dto.FavoriteRequest;
import io.restassured.builder.MultiPartSpecBuilder;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.MultiPartSpecification;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.util.MimeTypeUtils;

@DisplayName("게시글 통합 테스트")
public class ArticleAcceptanceTest extends AcceptanceTest {

    ArticleRequest articleRequest;

    @BeforeEach
    @Override
    public void setUp() {
        super.setUp();
        BookRequest toby = BookRequest.builder()
            .bookSummary("책 설명")
            .bookPubDate("2021-12-12")
            .bookIsbn("12345678910")
            .bookListPrice("10000")
            .bookThumbnail("썸네일")
            .bookTitle("토비의 스프링")
            .bookPublisher("허브출판사")
            .bookAuthor("이일민")
            .build();

        articleRequest = ArticleRequest.builder()
            .bookRequest(toby)
            .title("토비의 스프링 팝니다~")
            .description("책 설명")
            .bookStatus("BEST")
            .sellingLocation("BUSAN")
            .price("5000")
            .build();
    }

    @DisplayName("게시글을 등록한다.")
    @Test
    void createArticle() {
        String jwt = UserAcceptanceStep.requestToLoginAndGetAccessToken(loginRequest);
        ExtractableResponse<Response> res = ArticleAcceptanceStep.requestToCreateArticle(jwt,
            articleRequest);
        AcceptanceStep.assertThatStatusIsCreated(res);
    }

    @DisplayName("게시글을 상세 조회한다. (나의 게시글, 관심목록 X)")
    @Test
    void findArticle_isMyArticleAndIsNotMyFavorite() {
        String jwt = UserAcceptanceStep.requestToLoginAndGetAccessToken(loginRequest);

        Long articleId = AcceptanceUtils.getIdFromResponse(
            ArticleAcceptanceStep.requestToCreateArticle(jwt, articleRequest));

        ExtractableResponse<Response> response = ArticleAcceptanceStep.requestToFindArticle(jwt, articleId);
        ArticleResponse articleResponse = response.jsonPath().getObject(".", ArticleResponse.class);

        AcceptanceStep.assertThatStatusIsOk(response);
        ArticleAcceptanceStep.assertThatFindArticle(articleResponse, articleRequest, user, true, false);
    }

    @DisplayName("게시글을 상세 조회한다. (다른 유저의 게시글, 관심목록 O)")
    @Test
    void findArticle_isNotMyArticleAndIsMyFavorite() {
        String anotherUserJwt = UserAcceptanceStep.requestToLoginAndGetAccessToken(
            anotherLoginRequest);
        Long articleId = AcceptanceUtils.getIdFromResponse(
            ArticleAcceptanceStep.requestToCreateArticle(anotherUserJwt, articleRequest));

        String jwt = UserAcceptanceStep.requestToLoginAndGetAccessToken(loginRequest);
        UserAcceptanceStep.addFavorite(jwt, new FavoriteRequest(articleId));

        ExtractableResponse<Response> response = ArticleAcceptanceStep.requestToFindArticle(jwt, articleId);
        ArticleResponse articleResponse = response.jsonPath().getObject(".", ArticleResponse.class);

        AcceptanceStep.assertThatStatusIsOk(response);
        ArticleAcceptanceStep.assertThatFindArticle(articleResponse, articleRequest, anotherUser, false, true);
    }

    @DisplayName("게시글을 두번 상세 조회한다. (조회수 +2 확인)")
    @Test
    void findMyArticle_twice() {
        String jwt = UserAcceptanceStep.requestToLoginAndGetAccessToken(loginRequest);

        Long articleId = AcceptanceUtils.getIdFromResponse(
            ArticleAcceptanceStep.requestToCreateArticle(jwt, articleRequest));

        ArticleAcceptanceStep.requestToFindArticle(jwt, articleId);
        ExtractableResponse<Response> response = ArticleAcceptanceStep.requestToFindArticle(jwt, articleId);
        ArticleResponse articleResponse = response.jsonPath().getObject(".", ArticleResponse.class);

        AcceptanceStep.assertThatStatusIsOk(response);
        ArticleAcceptanceStep.assertThatFindArticle(articleResponse, articleRequest, user, true, false);
        assertThat(articleResponse.getViewCount()).isEqualTo(2L);
    }

    @DisplayName("로그인한 유저가 게시글을 전체 조회한다.")
    @Test
    void findArticles_loginUser() {
        ArticlesRequest articlesRequest = ArticlesRequest.builder()
            .title("스프링")
            .build();

        Pagination pagination = new Pagination(0, 10);

        String jwt = UserAcceptanceStep.requestToLoginAndGetAccessToken(loginRequest);
        ArticleAcceptanceStep.requestToCreateArticle(jwt, articleRequest);

        ExtractableResponse<Response> response = ArticleAcceptanceStep.requestToFindArticles(jwt,
            articlesRequest, pagination);
        List<ArticlesResponse> articlesRespons = response.jsonPath()
            .getList(".", ArticlesResponse.class);

        AcceptanceStep.assertThatStatusIsOk(response);
        ArticleAcceptanceStep.assertThatFindArticles(articlesRespons, articleRequest);
    }

    @DisplayName("로그인하지 않은 유저가 게시글을 전체 조회한다.")
    @Test
    void findArticles_anonymousUser() {
        ArticlesRequest articlesRequest = ArticlesRequest.builder()
            .title("스프링")
            .sellingLocation("BUSAN")
            .build();

        Pagination pagination = new Pagination(0, 10);

        String jwt = UserAcceptanceStep.requestToLoginAndGetAccessToken(loginRequest);
        ArticleAcceptanceStep.requestToCreateArticle(jwt, articleRequest);

        ExtractableResponse<Response> response = ArticleAcceptanceStep.requestToFindArticles(
            "anonymousUser",
            articlesRequest, pagination);
        List<ArticlesResponse> articlesRespons = response.jsonPath()
            .getList(".", ArticlesResponse.class);

        AcceptanceStep.assertThatStatusIsOk(response);
        ArticleAcceptanceStep.assertThatFindArticles(articlesRespons, articleRequest);
    }

    @DisplayName("ISBN 으로 네이버 책(API)을 검색한다.")
    @Test
    void findNaverBooks_isbn() {
        NaverBookRequest naverBookRequest = NaverBookRequest.builder()
            .isbn("8960773433")
            .display(10)
            .start(1)
            .build();

        String jwt = UserAcceptanceStep.requestToLoginAndGetAccessToken(loginRequest);

        ExtractableResponse<Response> response = ArticleAcceptanceStep.requestToFindNaverBooks(jwt,
            naverBookRequest);
        BookResponse bookResponse = response.jsonPath().getList(".", BookResponse.class).get(0);

        AcceptanceStep.assertThatStatusIsOk(response);
        ArticleAcceptanceStep.assertThatFindNaverBooks(bookResponse);
    }

    @DisplayName("제목으로 네이버 책(API)을 검색한다.")
    @Test
    void findNaverBooks_title() {
        NaverBookRequest naverBookRequest = NaverBookRequest.builder()
            .title("토비")
            .display(10)
            .start(1)
            .build();

        String jwt = UserAcceptanceStep.requestToLoginAndGetAccessToken(loginRequest);

        ExtractableResponse<Response> response = ArticleAcceptanceStep.requestToFindNaverBooks(jwt,
            naverBookRequest);
        List<BookResponse> bookResponses = response.jsonPath().getList(".", BookResponse.class);

        AcceptanceStep.assertThatStatusIsOk(response);
        assertThat(bookResponses.size()).isEqualTo(naverBookRequest.getDisplay());
    }

    @DisplayName("게시글을 수정한다.")
    @Test
    void updateArticle() {
        String jwt = UserAcceptanceStep.requestToLoginAndGetAccessToken(loginRequest);

        Long articleId = AcceptanceUtils.getIdFromResponse(
            ArticleAcceptanceStep.requestToCreateArticle(jwt, articleRequest));
        String deleteImgUrl = ArticleAcceptanceStep.requestToFindArticle(jwt, articleId).jsonPath()
            .getObject(".", ArticleResponse.class).getImages().get(0);

        ArticleUpdateRequest updateRequest = ArticleUpdateRequest.builder()
            .title("토비의 스프링 팝니다~ (수정)")
            .description("책 설명 (수정)")
            .bookStatus(BookStatus.MIDDLE.toString())
            .price("25000")
            .sellingLocation("INCHEON")
            .deleteImgUrls(List.of(deleteImgUrl))
            .build();

        MultiPartSpecification image = new MultiPartSpecBuilder(
            "updateImage1".getBytes())
            .mimeType(MimeTypeUtils.IMAGE_JPEG.toString())
            .controlName("images")
            .fileName("updateImage1.jpg")
            .build();
        List<MultiPartSpecification> images = List.of(image);

        ExtractableResponse<Response> response = ArticleAcceptanceStep.requestToUpdateArticle(jwt, articleId,
            updateRequest, images);

        ArticleResponse articleResponse = ArticleAcceptanceStep.requestToFindArticle(jwt, articleId).jsonPath()
            .getObject(".", ArticleResponse.class);

        AcceptanceStep.assertThatStatusIsOk(response);
        ArticleAcceptanceStep.assertThatUpdateArticle(articleResponse, updateRequest, 2);
    }

    @DisplayName("게시글 상태를 변경한다.")
    @Test
    void updateArticleStatus() {
        ArticleStatusUpdateRequest request = new ArticleStatusUpdateRequest(
            ArticleStatus.RESERVED.toString());

        String jwt = UserAcceptanceStep.requestToLoginAndGetAccessToken(loginRequest);
        Long articleId = AcceptanceUtils.getIdFromResponse(
            ArticleAcceptanceStep.requestToCreateArticle(jwt, articleRequest));

        ExtractableResponse<Response> response = ArticleAcceptanceStep.requestToUpdateArticleStatus(
            jwt, articleId, request);
        String articleStatus = ArticleAcceptanceStep.requestToFindArticle(jwt, articleId).jsonPath()
            .getObject(".", ArticleResponse.class).getArticleStatus();

        AcceptanceStep.assertThatStatusIsOk(response);
        assertThat(articleStatus).isEqualTo(ArticleStatus.valueOf(request.getArticleStatus()).getName());
    }

    @DisplayName("게시글을 삭제한다.")
    @Test
    void deleteArticle() {
        String jwt = UserAcceptanceStep.requestToLoginAndGetAccessToken(loginRequest);
        Long articleId = AcceptanceUtils.getIdFromResponse(
            ArticleAcceptanceStep.requestToCreateArticle(jwt, articleRequest));

        ExtractableResponse<Response> response = ArticleAcceptanceStep.requestToDeleteArticle(jwt,
            articleId);
        List<ArticlesResponse> articlesRespons = ArticleAcceptanceStep.requestToFindArticles(
                jwt, new ArticlesRequest(), new Pagination(0, 10))
            .jsonPath().getList(".", ArticlesResponse.class);

        AcceptanceStep.assertThatStatusIsOk(response);
        assertThat(articlesRespons.size()).isEqualTo(0);
    }
}
