package com.bookwhale.article.acceptance.step;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.bookwhale.article.dto.ArticleRequest;
import com.bookwhale.common.domain.Location;
import com.bookwhale.common.dto.Pagination;
import com.bookwhale.article.domain.BookStatus;
import com.bookwhale.article.domain.ArticleStatus;
import com.bookwhale.article.dto.BookResponse;
import com.bookwhale.article.dto.NaverBookRequest;
import com.bookwhale.article.dto.ArticleResponse;
import com.bookwhale.article.dto.ArticleStatusUpdateRequest;
import com.bookwhale.article.dto.ArticleUpdateRequest;
import com.bookwhale.article.dto.ArticlesRequest;
import com.bookwhale.article.dto.ArticlesResponse;
import com.bookwhale.user.domain.User;
import io.restassured.builder.MultiPartSpecBuilder;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.MultiPartSpecification;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.MimeTypeUtils;

public class ArticleAcceptanceStep {

    public static void assertThatFindArticle(ArticleResponse res, ArticleRequest req, User seller,
        boolean isMyArticle, boolean isMyFavorite) {
        Assertions.assertAll(
            () -> assertThat(res.getSellerId()).isEqualTo(seller.getId()),
            () -> assertThat(res.getSellerIdentity()).isEqualTo(seller.getIdentity()),
            () -> assertThat(res.getSellerProfileImage()).isEqualTo(seller.getProfileImage()),
            () -> assertThat(res.getTitle()).isEqualTo(req.getTitle()),
            () -> assertThat(res.getPrice()).isEqualTo(req.getPrice()),
            () -> assertThat(res.getDescription()).isEqualTo(req.getDescription()),
            () -> assertThat(res.getArticleStatus()).isEqualTo(ArticleStatus.SALE.getName()),
            () -> assertThat(res.getTitle()).isEqualTo(req.getTitle()),
            () -> assertThat(res.isMyArticle()).isEqualTo(isMyArticle),
            () -> assertThat(res.isMyFavorite()).isEqualTo(isMyFavorite),
            () -> assertThat(res.getBeforeTime()).isNotNull(),
            () -> assertThat(res.getBookStatus()).isEqualTo(
                BookStatus.valueOf(req.getBookStatus()).getName()),
            () -> assertThat(res.getBookResponse().getBookIsbn()).isEqualTo(
                req.getBookRequest().getBookIsbn()),
            () -> assertThat(res.getBookResponse().getBookAuthor()).isEqualTo(
                req.getBookRequest().getBookAuthor()),
            () -> assertThat(res.getBookResponse().getBookTitle()).isEqualTo(
                req.getBookRequest().getBookTitle()),
            () -> assertThat(res.getBookResponse().getBookPublisher()).isEqualTo(
                req.getBookRequest().getBookPublisher()),
            () -> assertThat(res.getBookResponse().getBookSummary()).isEqualTo(
                req.getBookRequest().getBookSummary()),
            () -> assertThat(res.getBookResponse().getBookThumbnail()).isEqualTo(
                req.getBookRequest().getBookThumbnail()),
            () -> assertThat(res.getBookResponse().getBookPubDate()).isEqualTo(
                req.getBookRequest().getBookPubDate()),
            () -> assertThat(res.getBookResponse().getBookListPrice()).isEqualTo(
                req.getBookRequest().getBookListPrice()),
            () -> assertThat(res.getViewCount()).isNotEqualTo(0L)
        );
    }

    public static void assertThatFindArticles(List<ArticlesResponse> res, ArticleRequest req) {
        Assertions.assertAll(
            () -> assertThat(res.size()).isEqualTo(1),
            () -> assertThat(res.get(0).getArticlePrice()).isEqualTo(req.getPrice()),
            () -> assertThat(res.get(0).getArticleTitle()).isEqualTo(req.getTitle()),
            () -> assertThat(res.get(0).getArticlePrice()).isEqualTo(req.getPrice()),
            () -> assertThat(res.get(0).getBeforeTime()).isNotNull(),
            () -> assertThat(res.get(0).getArticleStatus()).isEqualTo(ArticleStatus.SALE.getName()),
            () -> assertThat(res.get(0).getSellingLocation()).isEqualTo(Location.BUSAN.getName()),
            () -> assertThat(res.get(0).getArticleImage()).isNotNull(),
            () -> assertThat(res.get(0).getViewCount()).isEqualTo(0),
            () -> assertThat(res.get(0).getFavoriteCount()).isEqualTo(0)
        );
    }

    public static void assertThatFindNaverBooks(BookResponse bookResponse) {
        Assertions.assertAll(
            () -> assertThat(bookResponse.getBookTitle()).isEqualTo(
                "토비의 스프링 3.1 세트 (스프링의 이해와 원리 + 스프링의 기술과 선택, 전2권)"),
            () -> assertThat(bookResponse.getBookAuthor()).isEqualTo("이일민")
        );
    }

    public static void assertThatUpdateArticle(ArticleResponse res, ArticleUpdateRequest req, int size) {
        Assertions.assertAll(
            () -> assertThat(res.getTitle()).isEqualTo(req.getTitle()),
            () -> assertThat(res.getDescription()).isEqualTo(req.getDescription()),
            () -> assertThat(res.getPrice()).isEqualTo(req.getPrice()),
            () -> assertThat(res.getBookStatus()).isEqualTo(
                BookStatus.valueOf(req.getBookStatus()).getName()),
            () -> assertThat(res.getSellingLocation()).isEqualTo(Location.valueOf(req.getSellingLocation()).getName()),
            () -> assertThat(res.getImages().size()).isEqualTo(size)
        );
    }

    public static ExtractableResponse<Response> requestToCreateArticle(String jwt,
        ArticleRequest articleRequest) {
        MultiPartSpecification image1 = new MultiPartSpecBuilder(
            "image1".getBytes())
            .mimeType(MimeTypeUtils.IMAGE_JPEG.toString())
            .controlName("images")
            .fileName("image1.jpg")
            .build();

        MultiPartSpecification image2 = new MultiPartSpecBuilder(
            "image2".getBytes())
            .mimeType(MimeTypeUtils.IMAGE_JPEG.toString())
            .controlName("images")
            .fileName("image2.jpg")
            .build();

        MultiPartSpecification json = new MultiPartSpecBuilder(articleRequest)
            .controlName("articleRequest")
            .charset(StandardCharsets.UTF_8)
            .mimeType(MimeTypeUtils.APPLICATION_JSON_VALUE).build();

        return given().log().all()
            .header(HttpHeaders.AUTHORIZATION, jwt)
            .contentType(MediaType.MULTIPART_MIXED_VALUE)
            .multiPart(image1)
            .multiPart(image2)
            .multiPart(json)
            .when()
            .post("/api/article")
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> requestToFindArticle(String jwt, Long articleId) {
        return given().log().all()
            .header(HttpHeaders.AUTHORIZATION, jwt)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("/api/article/{articleId}", articleId)
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> requestToFindArticles(String jwt, ArticlesRequest req,
        Pagination page) {
        return given().log().all()
            .header(HttpHeaders.AUTHORIZATION, jwt)
            .when()
            .get("/api/article?"
                + (req.getTitle() != null ? "title=" + req.getTitle() + "&" : "")
                + (req.getAuthor() != null ? "author=" + req.getAuthor() + "&" : "")
                + (req.getPublisher() != null ? "publisher=" + req.getPublisher() + "&" : "")
                + (req.getSellingLocation() != null ? "sellingLocation=" + req.getSellingLocation()
                + "&" : "")
                + (req.getArticleStatus() != null ? "articleStatus=" + req.getArticleStatus() + "&" : "")
                + "page=" + page.getPage()
                + "&size=" + page.getSize())
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> requestToFindNaverBooks(String jwt,
        NaverBookRequest req) {
        return given().log().all()
            .header(HttpHeaders.AUTHORIZATION, jwt)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(req)
            .when()
            .get("/api/article/naverBookAPI?"
                + (req.getTitle() == null ? "" : "title=" + req.getTitle())
                + (req.getIsbn() == null ? "" : "isbn=" + req.getIsbn())
                + (req.getAuthor() == null ? "" : "author=" + req.getAuthor())
                + "&display=" + req.getDisplay()
                + "&start=" + req.getStart()
            )
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> requestToUpdateArticle(String jwt,
        Long articleId, ArticleUpdateRequest request, List<MultiPartSpecification> images) {

        MultiPartSpecification json = new MultiPartSpecBuilder(request)
            .controlName("articleUpdateRequest")
            .charset(StandardCharsets.UTF_8)
            .mimeType(MimeTypeUtils.APPLICATION_JSON_VALUE).build();

        return given().log().all()
            .header(HttpHeaders.AUTHORIZATION, jwt)
            .contentType(MediaType.MULTIPART_MIXED_VALUE)
            .multiPart(images.get(0))
            .multiPart(json)
            .when()
            .patch("/api/article/{articleId}", articleId)
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> requestToUpdateArticleStatus(String jwt, Long articleId,
        ArticleStatusUpdateRequest request) {
        return given().log().all()
            .header(HttpHeaders.AUTHORIZATION, jwt)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(request)
            .when()
            .patch("/api/article/articleStatus/{articleId}", articleId)
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> requestToDeleteArticle(String jwt, Long articleId) {
        return given().log().all()
            .header(HttpHeaders.AUTHORIZATION, jwt)
            .when()
            .delete("/api/article/{articleId}", articleId)
            .then().log().all()
            .extract();
    }
}