package com.bookwhale.article.service;

import static java.util.List.of;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.util.Lists.emptyList;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bookwhale.article.domain.Article;
import com.bookwhale.article.domain.ArticleRepository;
import com.bookwhale.article.domain.ArticleStatus;
import com.bookwhale.article.domain.BookStatus;
import com.bookwhale.article.dto.ArticleRequest;
import com.bookwhale.article.dto.ArticleResponse;
import com.bookwhale.article.dto.ArticleStatusUpdateRequest;
import com.bookwhale.article.dto.ArticleUpdateRequest;
import com.bookwhale.article.dto.BookRequest;
import com.bookwhale.common.domain.Location;
import com.bookwhale.common.exception.CustomException;
import com.bookwhale.common.exception.ErrorCode;
import com.bookwhale.common.upload.FileUploader;
import com.bookwhale.favorite.domain.FavoriteRepository;
import com.bookwhale.user.domain.User;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
@DisplayName("게시글 단위 테스트(Service)")
public class ArticleServiceTest {

    @Mock
    ArticleRepository articleRepository;

    @Mock
    FileUploader fileUploader;

    @Mock
    FavoriteRepository favoriteRepository;

    ArticleService articleService;

    ArticleRequest articleRequest;

    User user;

    @BeforeEach
    void setUp() {
        articleService = new ArticleService(articleRepository, fileUploader, favoriteRepository);

        BookRequest bookRequest = BookRequest.builder()
            .bookSummary("설명")
            .bookPubDate("2021-12-12")
            .bookIsbn("12398128745902")
            .bookListPrice("10000")
            .bookThumbnail("썸네일")
            .bookTitle("토비의 스프링")
            .bookPublisher("출판사")
            .bookAuthor("이일민")
            .build();

        articleRequest = ArticleRequest.builder()
            .bookRequest(bookRequest)
            .title("책 팝니다~")
            .description("쿨 거래시 1000원 할인해드려요~")
            .bookStatus("BEST")
            .price("5000")
            .sellingLocation("JEJU")
            .build();

        user = User.builder()
            .id(1L)
            .nickname("남상우")
            .email("highright96@email.com")
            .build();
    }

    @DisplayName("게시글을 등록한다.")
    @Test
    void createArticle() {
        Article article = Article.create(user, articleRequest.toEntity());
        List<String> images = of("image1", "image2");

        when(fileUploader.uploadFiles(any())).thenReturn(images);
        when(articleRepository.save(any())).thenReturn(article);

        articleService.createArticle(user, articleRequest,
            of(new MockMultipartFile("images", "image".getBytes(StandardCharsets.UTF_8))));

        verify(articleRepository).save(any());
        verify(fileUploader).uploadFiles(any());
    }

    @DisplayName("나의 게시글을 상세 조회한다. (게시글 이미지 2개)")
    @Test
    void findMyArticle_success() {
        Article article = Article.create(user, articleRequest.toEntity());
        List<String> images = of("image1", "image2");
        article.getImages().addAll(article, images);
        article.setCreatedDate(LocalDateTime.now());

        when(articleRepository.findArticleWithSellerById(any())).thenReturn(Optional.of(article));
        when(favoriteRepository.existsByUserAndArticle(any(), any())).thenReturn(true);

        ArticleResponse response = articleService.findArticle(user, 1L);

        verify(articleRepository).findArticleWithSellerById(any());
        assertAll(
            () -> assertThat(response.getTitle()).isEqualTo(articleRequest.getTitle()),
            () -> assertThat(response.getPrice()).isEqualTo(articleRequest.getPrice()),
            () -> assertThat(response.getDescription()).isEqualTo(articleRequest.getDescription()),
            () -> assertThat(response.getArticleStatus()).isEqualTo(ArticleStatus.SALE.getName()),
            () -> assertThat(response.getTitle()).isEqualTo(articleRequest.getTitle()),
            () -> assertThat(response.isMyArticle()).isEqualTo(true),
            () -> assertThat(response.isMyFavorite()).isEqualTo(true),
            () -> assertThat(response.getBookStatus()).isEqualTo(
                BookStatus.valueOf(articleRequest.getBookStatus()).getName()),
            () -> assertThat(response.getImages().size()).isEqualTo(2),
            () -> assertThat(response.getBookResponse().getBookIsbn()).isEqualTo(
                articleRequest.getBookRequest().getBookIsbn()),
            () -> assertThat(response.getBookResponse().getBookAuthor()).isEqualTo(
                articleRequest.getBookRequest().getBookAuthor()),
            () -> assertThat(response.getBookResponse().getBookTitle()).isEqualTo(
                articleRequest.getBookRequest().getBookTitle()),
            () -> assertThat(response.getBookResponse().getBookPublisher()).isEqualTo(
                articleRequest.getBookRequest().getBookPublisher()),
            () -> assertThat(response.getBookResponse().getBookSummary()).isEqualTo(
                articleRequest.getBookRequest().getBookSummary()),
            () -> assertThat(response.getBookResponse().getBookThumbnail()).isEqualTo(
                articleRequest.getBookRequest().getBookThumbnail()),
            () -> assertThat(response.getBookResponse().getBookPubDate()).isEqualTo(
                articleRequest.getBookRequest().getBookPubDate()),
            () -> assertThat(response.getBookResponse().getBookListPrice()).isEqualTo(
                articleRequest.getBookRequest().getBookListPrice())
        );
    }

    @DisplayName("다른 유저의 게시글을 상세 조회한다. (게시글 이미지 0개)")
    @Test
    void findNotMyArticle_success() {
        User otherUser = User.builder().id(2L).build();
        Article article = Article.create(user, articleRequest.toEntity());
        article.setCreatedDate(LocalDateTime.now());

        when(articleRepository.findArticleWithSellerById(any())).thenReturn(ofNullable(article));

        ArticleResponse response = articleService.findArticle(otherUser, 1L);

        verify(articleRepository).findArticleWithSellerById(any());
        assertAll(
            () -> assertThat(response.isMyArticle()).isEqualTo(false),
            () -> assertThat(response.getImages().isEmpty()).isEqualTo(true)
        );
    }

    @DisplayName("게시글을 상세 조회할 때 조회수가 +1 증가된다.")
    @Test
    void increaseViewCountAfterFindArticle() {
        User otherUser = User.builder().id(2L).build();
        Article article = Article.create(user, articleRequest.toEntity());
        article.setCreatedDate(LocalDateTime.now());
        Long beforeViewCount = article.getViewCount();

        when(articleRepository.findArticleWithSellerById(any())).thenReturn(ofNullable(article));

        ArticleResponse response = articleService.findArticle(otherUser, 1L);

        verify(articleRepository).findArticleWithSellerById(any());
        assertAll(
            () -> assertThat(response.isMyArticle()).isEqualTo(false),
            () -> assertThat(article.getViewCount()).isEqualTo(beforeViewCount + 1L)
        );
    }

    @DisplayName("잘못된 게시글 ID로 상세 조회하면 예외가 발생한다.")
    @Test
    void findArticle_invalidArticleId_failure() {
        when(articleRepository.findArticleWithSellerById(any())).thenReturn(empty());

        assertThatThrownBy(() -> articleService.findArticle(user, 1L))
            .isInstanceOf(CustomException.class)
            .hasMessage(ErrorCode.INVALID_ARTICLE_ID.getMessage());
    }

    @DisplayName("게시글을 수정한다. (게시글 이미지 1개, 삭제하는 이미지 0개, 추가하는 이미지 1개 -> 총 2개)")
    @Test
    void updateArticle_One_success() {
        Article article = Article.create(user, articleRequest.toEntity());
        article.getImages().addAll(article, of("image1"));

        ArticleUpdateRequest request = ArticleUpdateRequest.builder()
            .title("이펙티브 자바")
            .description("이펙티브 자바입니다.")
            .price("15000")
            .bookStatus("BEST")
            .sellingLocation("GWANGJU")
            .build();

        List<MultipartFile> images = of(
            new MockMultipartFile("images", "image1".getBytes(StandardCharsets.UTF_8))
        );

        when(articleRepository.findById(any())).thenReturn(Optional.of(article));
        when(fileUploader.uploadFiles(any())).thenReturn(of("image1"));

        articleService.updateArticle(user, 1L, request, images);

        verify(articleRepository).findById(any());
        verify(fileUploader, never()).deleteFile(any());
        verify(fileUploader).uploadFiles(any());
        assertAll(
            () -> assertThat(article.getTitle()).isEqualTo(request.getTitle()),
            () -> assertThat(article.getPrice()).isEqualTo(request.getPrice()),
            () -> assertThat(article.getDescription()).isEqualTo(request.getDescription()),
            () -> assertThat(article.getSellingLocation().getName()).isEqualTo(
                Location.valueOf(request.getSellingLocation()).getName()),
            () -> Assertions.assertThat(article.getImages().getSize()).isEqualTo(2),
            () -> assertThat(article.getBookStatus()).isEqualTo(
                BookStatus.valueOf(request.getBookStatus()))
        );
    }

    @DisplayName("게시글을 수정한다. (게시글 이미지 3개, 삭제하는 이미지 1개, 추가하는 이미지 1개 -> 총 3개)")
    @Test
    void updateArticle_Two_success() {
        Article article = Article.create(user, articleRequest.toEntity());
        article.getImages().addAll(article, of("image1", "image2", "image3"));

        ArticleUpdateRequest request = ArticleUpdateRequest.builder()
            .title("이펙티브 자바")
            .description("이펙티브 자바입니다.")
            .price("15000")
            .bookStatus("BEST")
            .sellingLocation("JEJU")
            .deleteImgUrls(List.of("image2"))
            .build();

        List<MultipartFile> images = of(
            new MockMultipartFile("images", "image4".getBytes(StandardCharsets.UTF_8))
        );

        when(articleRepository.findById(any())).thenReturn(Optional.of(article));
        doNothing().when(fileUploader).deleteFiles(anyList());
        when(fileUploader.uploadFiles(any())).thenReturn(of("image4"));

        articleService.updateArticle(user, 1L, request, images);

        verify(articleRepository).findById(any());
        verify(fileUploader).deleteFiles(anyList());
        verify(fileUploader).uploadFiles(any());
        assertAll(
            () -> assertThat(article.getTitle()).isEqualTo(request.getTitle()),
            () -> assertThat(article.getPrice()).isEqualTo(request.getPrice()),
            () -> assertThat(article.getDescription()).isEqualTo(request.getDescription()),
            () -> Assertions.assertThat(article.getImages().getSize()).isEqualTo(3),
            () -> assertThat(article.getBookStatus()).isEqualTo(
                BookStatus.valueOf(request.getBookStatus()))
        );
    }

    @DisplayName("게시글을 수정한다. (게시글 이미지 3개, 삭제하는 이미지 2개, 추가하는 이미지 0개 -> 총 1개)")
    @Test
    void updateArticle_Three_success() {
        Article article = Article.create(user, articleRequest.toEntity());
        article.getImages().addAll(article, of("image1", "image2", "image3"));

        ArticleUpdateRequest request = ArticleUpdateRequest.builder()
            .title("이펙티브 자바")
            .description("이펙티브 자바입니다.")
            .price("15000")
            .bookStatus("BEST")
            .sellingLocation("JEJU")
            .deleteImgUrls(List.of("image2", "image3"))
            .build();

        when(articleRepository.findById(any())).thenReturn(Optional.of(article));
        doNothing().when(fileUploader).deleteFiles(anyList());

        articleService.updateArticle(user, 1L, request, emptyList());

        verify(articleRepository).findById(any());
        verify(fileUploader).deleteFiles(anyList());
        verify(fileUploader, never()).uploadFiles(any());
        assertAll(
            () -> assertThat(article.getTitle()).isEqualTo(request.getTitle()),
            () -> assertThat(article.getPrice()).isEqualTo(request.getPrice()),
            () -> assertThat(article.getDescription()).isEqualTo(request.getDescription()),
            () -> Assertions.assertThat(article.getImages().getSize()).isEqualTo(1),
            () -> assertThat(article.getBookStatus()).isEqualTo(
                BookStatus.valueOf(request.getBookStatus()))
        );
    }

    @DisplayName("잘못된 article_id로 게시글을 수정하면 예외가 발생한다.")
    @Test
    void updateArticle_invalidArticleId_failure() {
        ArticleUpdateRequest request = ArticleUpdateRequest.builder().build();
        List<MultipartFile> images = emptyList();

        when(articleRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> articleService.updateArticle(user, 1L, request, images))
            .isInstanceOf(CustomException.class)
            .hasMessage(ErrorCode.INVALID_ARTICLE_ID.getMessage());
    }

    @DisplayName("권한이 없는 유저가 게시글을 수정하면 예외가 발생한다.")
    @Test
    void updateArticle_invalidUser_failure() {
        User otherUser = User.builder().id(2L).build();
        Article article = Article.create(otherUser, articleRequest.toEntity());
        ArticleUpdateRequest request = ArticleUpdateRequest.builder().build();
        List<MultipartFile> images = emptyList();

        when(articleRepository.findById(any())).thenReturn(Optional.ofNullable(article));

        assertThatThrownBy(() -> articleService.updateArticle(user, 1L, request, images))
            .isInstanceOf(CustomException.class)
            .hasMessage(ErrorCode.USER_ACCESS_DENIED.getMessage());
    }

    @DisplayName("게시글 상태를 변경한다.")
    @Test
    void updateArticleStatus_success() {
        Article article = Article.create(user, articleRequest.toEntity());
        ArticleStatusUpdateRequest request = new ArticleStatusUpdateRequest(
            ArticleStatus.SOLD_OUT.toString());

        when(articleRepository.findById(any())).thenReturn(Optional.ofNullable(article));

        articleService.updateArticleStatus(user, 1L, request);

        verify(articleRepository).findById(any());
        assertThat(article.getArticleStatus()).isEqualTo(
            ArticleStatus.valueOf(request.getArticleStatus()));
    }

    @DisplayName("잘못된 article_id 로 게시글 상태를 변경하면 예외가 발생한다.")
    @Test
    void updateArticleStatus_invalidArticleId_failure() {
        ArticleStatusUpdateRequest request = new ArticleStatusUpdateRequest(
            ArticleStatus.SOLD_OUT.toString());

        when(articleRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> articleService.updateArticleStatus(user, 1L, request))
            .isInstanceOf(CustomException.class)
            .hasMessage(ErrorCode.INVALID_ARTICLE_ID.getMessage());
    }

    @DisplayName("권한이 없는 유저가 게시글 상태를 변경하면 예외가 발생한다.")
    @Test
    void updateArticleStatus_invalidUser_failure() {
        Article article = Article.create(user, articleRequest.toEntity());
        ArticleStatusUpdateRequest request = new ArticleStatusUpdateRequest(
            ArticleStatus.SOLD_OUT.toString());
        User otherUser = User.builder().id(2L).build();

        when(articleRepository.findById(any())).thenReturn(Optional.ofNullable(article));

        assertThatThrownBy(() -> articleService.updateArticleStatus(otherUser, 1L, request))
            .isInstanceOf(CustomException.class)
            .hasMessage(ErrorCode.USER_ACCESS_DENIED.getMessage());
    }
}
