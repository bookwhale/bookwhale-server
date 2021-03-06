package com.bookwhale.article.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import com.bookwhale.article.acceptance.step.ArticleAcceptanceStep;
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
import com.bookwhale.auth.domain.info.UserInfoFromToken;
import com.bookwhale.common.acceptance.AcceptanceTest;
import com.bookwhale.common.acceptance.AcceptanceUtils;
import com.bookwhale.common.acceptance.step.AcceptanceStep;
import com.bookwhale.common.dto.Pagination;
import com.bookwhale.favorite.domain.Favorite;
import com.bookwhale.user.acceptance.step.UserAcceptanceStep;
import com.bookwhale.user.dto.FavoriteRequest;
import com.bookwhale.user.dto.FavoriteResponse;
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
    ArticleRequest articleRequestWithActiveN;

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

        articleRequestWithActiveN = ArticleRequest.builder()
            .bookRequest(toby)
            .title("토비의 스프링 2판 급처합니다.")
            .description("책 설명")
            .bookStatus("MIDDLE")
            .sellingLocation("SEOUL")
            .price("5000")
            .build();
    }

    @DisplayName("게시글을 등록한다.")
    @Test
    void createArticle() {

        String apiToken = UserAcceptanceStep.requestToLoginAndGetAccessToken(
            UserInfoFromToken.of(user), jwt);
        ExtractableResponse<Response> res = ArticleAcceptanceStep.requestToCreateArticle(apiToken,
            articleRequest);
        AcceptanceStep.assertThatStatusIsCreated(res);
    }

    @DisplayName("나의 게시글들을 조회한다.")
    @Test
    void findArticles_one() {
        ArticleRequest articleRequest = ArticleRequest.builder()
            .bookRequest(BookRequest.builder()
                .bookSummary("책 설명")
                .bookPubDate("2021-12-12")
                .bookIsbn("12345678910")
                .bookListPrice("10000")
                .bookThumbnail("썸네일")
                .bookTitle("토비의 스프링")
                .bookPublisher("허브출판사")
                .bookAuthor("이일민")
                .build())
            .title("토비의 스프링 팝니다~")
            .description("책 설명")
            .sellingLocation("DAEGU")
            .bookStatus("BEST")
            .price("5000")
            .build();

        String apiToken = UserAcceptanceStep.requestToLoginAndGetAccessToken(
            UserInfoFromToken.of(user), jwt);
        ArticleAcceptanceStep.requestToCreateArticle(apiToken, articleRequest);
        Long deactivatedArticleId = AcceptanceUtils.getIdFromResponse(
            ArticleAcceptanceStep.requestToCreateArticle(apiToken, articleRequestWithActiveN));

        // 등록한 판매글 삭제 (토비의 스프링 2판)
        ArticleAcceptanceStep.requestToDeleteArticle(
            apiToken,
            deactivatedArticleId);

        ExtractableResponse<Response> response = ArticleAcceptanceStep.requestToFindMyArticles(
            apiToken);
        List<ArticlesResponse> articlesResponse = response.jsonPath()
            .getList(".", ArticlesResponse.class);

        AcceptanceStep.assertThatStatusIsOk(response);
        ArticleAcceptanceStep.assertThatFindMyArticles(articlesResponse, articleRequest);
    }

    @DisplayName("나의 게시글들을 조회할 때 다른 유저의 게시글을 조회되지 않는다.")
    @Test
    void findArticles_empty() {
        ArticleRequest articleRequest = ArticleRequest.builder()
            .bookRequest(BookRequest.builder()
                .bookSummary("책 설명")
                .bookPubDate("2021-12-12")
                .bookIsbn("12345678910")
                .bookListPrice("10000")
                .bookThumbnail("썸네일")
                .bookTitle("토비의 스프링")
                .bookPublisher("허브출판사")
                .bookAuthor("이일민")
                .build())
            .title("토비의 스프링 팝니다~")
            .description("책 설명")
            .bookStatus("BEST")
            .price("5000")
            .build();

        String apiToken = UserAcceptanceStep.requestToLoginAndGetAccessToken(
            UserInfoFromToken.of(user), jwt);
        String anotherApiToken = UserAcceptanceStep.requestToLoginAndGetAccessToken(
            UserInfoFromToken.of(anotherUser), jwt);
        ArticleAcceptanceStep.requestToCreateArticle(anotherApiToken, articleRequest);

        ExtractableResponse<Response> response = ArticleAcceptanceStep.requestToFindMyArticles(
            apiToken);
        List<ArticlesResponse> articlesResponse = response.jsonPath()
            .getList(".", ArticlesResponse.class);

        AcceptanceStep.assertThatStatusIsOk(response);
        assertThat(articlesResponse.size()).isEqualTo(0);
    }

    @DisplayName("게시글을 상세 조회한다. (나의 게시글, 관심목록 X)")
    @Test
    void findArticle_isMyArticleAndIsNotMyFavorite() {
        String apiToken = UserAcceptanceStep.requestToLoginAndGetAccessToken(
            UserInfoFromToken.of(user), jwt);

        Long articleId = AcceptanceUtils.getIdFromResponse(
            ArticleAcceptanceStep.requestToCreateArticle(apiToken, articleRequest));

        ExtractableResponse<Response> response = ArticleAcceptanceStep.requestToFindArticle(
            apiToken,
            articleId);
        ArticleResponse articleResponse = response.jsonPath().getObject(".", ArticleResponse.class);

        AcceptanceStep.assertThatStatusIsOk(response);
        ArticleAcceptanceStep.assertThatFindArticle(articleResponse, articleRequest, user, true,
            false);
        assertThat(articleResponse.getViewCount()).isEqualTo(0); // 자신의 판매글에는 viewCount가 증가하지 않도록 변경
    }

    @DisplayName("게시글을 상세 조회한다. (다른 유저의 게시글, 관심목록 O)")
    @Test
    void findArticle_isNotMyArticleAndIsMyFavorite() {
        String anotherApiToken = UserAcceptanceStep.requestToLoginAndGetAccessToken(
            UserInfoFromToken.of(anotherUser), jwt);
        Long articleId = AcceptanceUtils.getIdFromResponse(
            ArticleAcceptanceStep.requestToCreateArticle(anotherApiToken, articleRequest));

        String apiToken = UserAcceptanceStep.requestToLoginAndGetAccessToken(
            UserInfoFromToken.of(user), jwt);
        UserAcceptanceStep.addFavorite(apiToken, new FavoriteRequest(articleId));

        ExtractableResponse<Response> response = ArticleAcceptanceStep.requestToFindArticle(
            apiToken,
            articleId);
        ArticleResponse articleResponse = response.jsonPath().getObject(".", ArticleResponse.class);

        AcceptanceStep.assertThatStatusIsOk(response);
        ArticleAcceptanceStep.assertThatFindArticle(articleResponse, articleRequest, anotherUser,
            false, true);
        assertThat(articleResponse.getViewCount()).isNotEqualTo(0);
    }

    @DisplayName("게시글을 두번 상세 조회한다. (조회수 +2 확인)")
    @Test
    void findMyArticle_twice() {
        String apiToken = UserAcceptanceStep.requestToLoginAndGetAccessToken(
            UserInfoFromToken.of(user), jwt);

        Long articleId = AcceptanceUtils.getIdFromResponse(
            ArticleAcceptanceStep.requestToCreateArticle(apiToken, articleRequest));

        String anotherUserApiToken = UserAcceptanceStep.requestToLoginAndGetAccessToken(
            UserInfoFromToken.of(anotherUser), jwt);

        ArticleAcceptanceStep.requestToFindArticle(anotherUserApiToken, articleId);
        ExtractableResponse<Response> response = ArticleAcceptanceStep.requestToFindArticle(
            anotherUserApiToken,
            articleId);
        ArticleResponse articleResponse = response.jsonPath().getObject(".", ArticleResponse.class);

        AcceptanceStep.assertThatStatusIsOk(response);
        ArticleAcceptanceStep.assertThatFindArticle(articleResponse, articleRequest, user, false,
            false);
        assertThat(articleResponse.getViewCount()).isEqualTo(2L);
    }

    @DisplayName("로그인한 유저가 게시글을 전체 조회한다.")
    @Test
    void findArticles_loginUser() {
        ArticlesRequest articlesRequest = ArticlesRequest.builder()
            .search("스프링")
            .build();

        Pagination pagination = new Pagination(0, 10);

        String apiToken = UserAcceptanceStep.requestToLoginAndGetAccessToken(
            UserInfoFromToken.of(user), jwt);
        ArticleAcceptanceStep.requestToCreateArticle(apiToken, articleRequest);

        // 삭제된 판매글 구현을 위한 등록 및 삭제 처리
        Long deactivatedArticleId = AcceptanceUtils.getIdFromResponse(
            ArticleAcceptanceStep.requestToCreateArticle(apiToken, articleRequestWithActiveN));
        ArticleAcceptanceStep.requestToDeleteArticle(
            apiToken,
            deactivatedArticleId);

        ExtractableResponse<Response> response = ArticleAcceptanceStep.requestToFindArticles(
            apiToken,
            articlesRequest, pagination);
        List<ArticlesResponse> articlesResponses = response.jsonPath()
            .getList(".", ArticlesResponse.class);

        AcceptanceStep.assertThatStatusIsOk(response);
        ArticleAcceptanceStep.assertThatFindArticles(articlesResponses, articleRequest);
    }

    @DisplayName("로그인하지 않은 유저가 게시글을 전체 조회한다.")
    @Test
    void findArticles_anonymousUser() {
        ArticlesRequest articlesRequest = ArticlesRequest.builder()
            .search("스프링")
            .build();

        Pagination pagination = new Pagination(0, 10);

        String apiToken = UserAcceptanceStep.requestToLoginAndGetAccessToken(
            UserInfoFromToken.of(user), jwt);
        ArticleAcceptanceStep.requestToCreateArticle(apiToken, articleRequest);

        // 삭제된 판매글 구현을 위한 등록 및 삭제 처리
        Long deactivatedArticleId = AcceptanceUtils.getIdFromResponse(
            ArticleAcceptanceStep.requestToCreateArticle(apiToken, articleRequestWithActiveN));
        ArticleAcceptanceStep.requestToDeleteArticle(
            apiToken,
            deactivatedArticleId);

        ExtractableResponse<Response> response = ArticleAcceptanceStep.requestToFindArticles(
            "anonymousUser",
            articlesRequest, pagination);
        List<ArticlesResponse> articlesResponses = response.jsonPath()
            .getList(".", ArticlesResponse.class);

        AcceptanceStep.assertThatStatusIsOk(response);
        ArticleAcceptanceStep.assertThatFindArticles(articlesResponses, articleRequest);
    }

    @DisplayName("ISBN 으로 네이버 책(API)을 검색한다.")
    @Test
    void findNaverBooks_isbn() {
        NaverBookRequest naverBookRequest = NaverBookRequest.builder()
            .isbn("8960773433")
            .display(10)
            .start(1)
            .build();

        String apiToken = UserAcceptanceStep.requestToLoginAndGetAccessToken(
            UserInfoFromToken.of(user), jwt);

        ExtractableResponse<Response> response = ArticleAcceptanceStep.requestToFindNaverBooks(
            apiToken,
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

        String apiToken = UserAcceptanceStep.requestToLoginAndGetAccessToken(
            UserInfoFromToken.of(user), jwt);

        ExtractableResponse<Response> response = ArticleAcceptanceStep.requestToFindNaverBooks(
            apiToken,
            naverBookRequest);
        List<BookResponse> bookResponses = response.jsonPath().getList(".", BookResponse.class);

        AcceptanceStep.assertThatStatusIsOk(response);
        assertThat(bookResponses.size()).isEqualTo(naverBookRequest.getDisplay());
    }

    @DisplayName("게시글을 수정한다.")
    @Test
    void updateArticle() {
        String apiToken = UserAcceptanceStep.requestToLoginAndGetAccessToken(
            UserInfoFromToken.of(user), jwt);

        Long articleId = AcceptanceUtils.getIdFromResponse(
            ArticleAcceptanceStep.requestToCreateArticle(apiToken, articleRequest));
        String deleteImgUrl = ArticleAcceptanceStep.requestToFindArticle(apiToken, articleId)
            .jsonPath()
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

        ExtractableResponse<Response> response = ArticleAcceptanceStep.requestToUpdateArticle(
            apiToken,
            articleId,
            updateRequest, images);

        ArticleResponse articleResponse = ArticleAcceptanceStep.requestToFindArticle(apiToken,
                articleId)
            .jsonPath()
            .getObject(".", ArticleResponse.class);

        AcceptanceStep.assertThatStatusIsOk(response);
        ArticleAcceptanceStep.assertThatUpdateArticle(articleResponse, updateRequest, 2);
    }

    @DisplayName("게시글 상태를 변경한다.")
    @Test
    void updateArticleStatus() {
        ArticleStatusUpdateRequest request = new ArticleStatusUpdateRequest(
            ArticleStatus.RESERVED.toString());

        String apiToken = UserAcceptanceStep.requestToLoginAndGetAccessToken(
            UserInfoFromToken.of(user), jwt);
        Long articleId = AcceptanceUtils.getIdFromResponse(
            ArticleAcceptanceStep.requestToCreateArticle(apiToken, articleRequest));

        ExtractableResponse<Response> response = ArticleAcceptanceStep.requestToUpdateArticleStatus(
            apiToken, articleId, request);
        String articleStatus = ArticleAcceptanceStep.requestToFindArticle(apiToken, articleId)
            .jsonPath()
            .getObject(".", ArticleResponse.class).getArticleStatus();

        AcceptanceStep.assertThatStatusIsOk(response);
        assertThat(articleStatus).isEqualTo(
            ArticleStatus.valueOf(request.getArticleStatus()).getName());
    }

    @DisplayName("게시글을 삭제한다. (activeYn = N 으로 변경)")
    @Test
    void deleteArticle() {
        ArticlesRequest articlesRequest = ArticlesRequest.builder()
            .search("스프링")
            .build();

        String apiToken = UserAcceptanceStep.requestToLoginAndGetAccessToken(
            UserInfoFromToken.of(user), jwt);
        Long articleId = AcceptanceUtils.getIdFromResponse(
            ArticleAcceptanceStep.requestToCreateArticle(apiToken, articleRequest));

        ExtractableResponse<Response> response = ArticleAcceptanceStep.requestToDeleteArticle(
            apiToken,
            articleId);
        List<ArticlesResponse> articlesResponses = ArticleAcceptanceStep.requestToFindArticles(
                apiToken, articlesRequest, new Pagination(0, 10))
            .jsonPath().getList(".", ArticlesResponse.class);

        AcceptanceStep.assertThatStatusIsOk(response);
        assertThat(articlesResponses.size()).isEqualTo(0);
    }

    @DisplayName("게시글에 좋아요를 추가하는 기능 확인")
    @Test
    void favoriteToArticle() {
        String apiToken = UserAcceptanceStep.requestToLoginAndGetAccessToken(
            UserInfoFromToken.of(user), jwt);

        Long articleId = AcceptanceUtils.getIdFromResponse(
            ArticleAcceptanceStep.requestToCreateArticle(apiToken, articleRequest));

        String anotherUserApiToken = UserAcceptanceStep.requestToLoginAndGetAccessToken(
            UserInfoFromToken.of(anotherUser), jwt);

        ExtractableResponse<Response> response = ArticleAcceptanceStep.requestAddFavoriteArticle(
            anotherUserApiToken,
            new FavoriteRequest(articleId)
        );// 게시글 좋아요 요청

        FavoriteResponse favoriteResponse = response.jsonPath()
            .getObject(".", FavoriteResponse.class);

        assertThat(favoriteResponse.getFavoriteId()).isNotNull();
    }

    @DisplayName("게시글에 좋아요를 추가하면 좋아요 수가 +1 처리된다.")
    @Test
    void favoritePlusOneAndFindArticle() {
        String apiToken = UserAcceptanceStep.requestToLoginAndGetAccessToken(
            UserInfoFromToken.of(user), jwt);

        Long articleId = AcceptanceUtils.getIdFromResponse(
            ArticleAcceptanceStep.requestToCreateArticle(apiToken, articleRequest));

        String anotherUserApiToken = UserAcceptanceStep.requestToLoginAndGetAccessToken(
            UserInfoFromToken.of(anotherUser), jwt);

        ArticleAcceptanceStep.requestAddFavoriteArticle(
            anotherUserApiToken,
            new FavoriteRequest(articleId)
            ); // 게시글 좋아요 요청
        ExtractableResponse<Response> response = ArticleAcceptanceStep.requestToFindArticle(
            anotherUserApiToken, articleId);
        ArticleResponse articleResponse = response.jsonPath().getObject(".", ArticleResponse.class);

        AcceptanceStep.assertThatStatusIsOk(response);
        ArticleAcceptanceStep.assertThatFindArticle(articleResponse, articleRequest, user, false,
            true);

        assertThat(articleResponse.getViewCount()).isEqualTo(1L);
        assertThat(articleResponse.getFavoriteCount()).isEqualTo(1L);
    }

    @DisplayName("게시글에 좋아요가 처리된 내용을 제거하면 좋아요 수가 감소한다.")
    @Test
    void removeFavoriteAndFindArticle() {
        String apiToken = UserAcceptanceStep.requestToLoginAndGetAccessToken(
            UserInfoFromToken.of(user), jwt);

        Long articleId = AcceptanceUtils.getIdFromResponse(
            ArticleAcceptanceStep.requestToCreateArticle(apiToken, articleRequest));

        String anotherUserApiToken = UserAcceptanceStep.requestToLoginAndGetAccessToken(
            UserInfoFromToken.of(anotherUser), jwt);

        ArticleAcceptanceStep.requestAddFavoriteArticle(
            anotherUserApiToken,
            new FavoriteRequest(articleId)
        ); // 게시글 좋아요 요청

        ExtractableResponse<Response> articleFavorite = ArticleAcceptanceStep.getArticleFavorite(
            anotherUserApiToken,
            new FavoriteRequest(articleId));

        FavoriteResponse favoriteByAnotherUser = articleFavorite.jsonPath().getObject(".", FavoriteResponse.class);

        ArticleAcceptanceStep.requestRemoveFavoriteArticle(
            anotherUserApiToken, favoriteByAnotherUser.getFavoriteId()
        ); // 게시글 좋아요 제거 요청
        ExtractableResponse<Response> response = ArticleAcceptanceStep.requestToFindArticle(
            anotherUserApiToken, articleId);
        ArticleResponse articleResponse = response.jsonPath().getObject(".", ArticleResponse.class);

        AcceptanceStep.assertThatStatusIsOk(response);
        ArticleAcceptanceStep.assertThatFindArticle(articleResponse, articleRequest, user, false,
            false);

        assertThat(articleResponse.getViewCount()).isEqualTo(1L);
        assertThat(articleResponse.getFavoriteCount()).isEqualTo(0);
    }

    @DisplayName("좋아요 목록을 조회한다.")
    @Test
    void getUserFavorites() {
        String apiToken = UserAcceptanceStep.requestToLoginAndGetAccessToken(
            UserInfoFromToken.of(user), jwt);

        Long articleId = AcceptanceUtils.getIdFromResponse(
            ArticleAcceptanceStep.requestToCreateArticle(apiToken, articleRequest));

        // 삭제된 판매글 구현을 위한 등록 처리
        Long deactivatedArticleId = AcceptanceUtils.getIdFromResponse(
            ArticleAcceptanceStep.requestToCreateArticle(apiToken, articleRequestWithActiveN));

        String anotherUserApiToken = UserAcceptanceStep.requestToLoginAndGetAccessToken(
            UserInfoFromToken.of(anotherUser), jwt);

        ArticleAcceptanceStep.requestAddFavoriteArticle(
            anotherUserApiToken,
            new FavoriteRequest(articleId)
        ); // 게시글 좋아요 요청

        ArticleAcceptanceStep.requestAddFavoriteArticle(
            anotherUserApiToken,
            new FavoriteRequest(articleId)
        ); // 게시글 좋아요 요청 (삭제될 게시글)

        ArticleAcceptanceStep.requestToDeleteArticle(
            apiToken,
            deactivatedArticleId); // 게시글 (논리) 삭제

        ExtractableResponse<Response> userFavories = ArticleAcceptanceStep.getUserFavories(
            anotherUserApiToken);
        List<FavoriteResponse> favoriteResponses = userFavories.jsonPath().getList(".", FavoriteResponse.class);

        AcceptanceStep.assertThatStatusIsOk(userFavories);

        assertThat(favoriteResponses.get(0).getFavoriteId()).isNotNull();
        assertThat(favoriteResponses.get(0).getArticlesResponse().getArticleId()).isEqualTo(articleId);
    }
}
