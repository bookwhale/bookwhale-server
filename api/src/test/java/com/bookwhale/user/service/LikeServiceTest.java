package com.bookwhale.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bookwhale.article.domain.Article;
import com.bookwhale.article.dto.ArticleRequest;
import com.bookwhale.common.exception.CustomException;
import com.bookwhale.common.exception.ErrorCode;
import com.bookwhale.like.domain.Like;
import com.bookwhale.like.domain.LikeRepository;
import com.bookwhale.article.domain.ArticleRepository;
import com.bookwhale.article.dto.BookRequest;
import com.bookwhale.user.domain.User;
import com.bookwhale.user.dto.LikeRequest;
import com.bookwhale.user.dto.LikeResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("관심목록 관련 기능 단위 테스트(Service)")
public class LikeServiceTest {

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private LikeRepository likeRepository;

    LikeService likeService;

    User user;

    @BeforeEach
    void setUp() {
        likeService = new LikeService(likeRepository, articleRepository);

        user = User.builder()
            .id(1L)
            .identity("highright96")
            .password("1234")
            .name("남상우")
            .email("highright96@email.com")
            .phoneNumber("010-1234-1234")
            .build();
    }

    @DisplayName("관심목록을 조회한다.")
    @Test
    void findLikes_success() {
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

        ArticleRequest articleRequest = ArticleRequest.builder()
            .bookRequest(bookRequest)
            .title("책 팝니다~")
            .description("쿨 거래시 1000원 할인해드려요~")
            .sellingLocation("SEOUL")
            .bookStatus("BEST")
            .price("5000")
            .build();
        Article article = Article.create(user, articleRequest.toEntity());
        article.setCreatedDate(LocalDateTime.now());

        when(likeRepository.findAllByUser(any())).thenReturn(List.of(Like.create(user, article)));

        List<LikeResponse> responses = likeService.findAllLikes(user);

        verify(likeRepository).findAllByUser(any());
        Assertions.assertAll(
            () -> assertThat(responses.size()).isEqualTo(1),
            () -> org.assertj.core.api.Assertions.assertThat(
                responses.get(0).getArticlesResponse().getArticleImage()).isNull(),
            () -> org.assertj.core.api.Assertions.assertThat(
                responses.get(0).getArticlesResponse().getArticleTitle()).isEqualTo(
                articleRequest.getTitle()),
            () -> org.assertj.core.api.Assertions.assertThat(
                responses.get(0).getArticlesResponse().getArticlePrice()).isEqualTo(
                articleRequest.getPrice())
        );
    }

    @DisplayName("관심목록에 추가한다.")
    @Test
    void addLike_success() {
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

        ArticleRequest articleRequest = ArticleRequest.builder()
            .bookRequest(bookRequest)
            .title("책 팝니다~")
            .description("쿨 거래시 1000원 할인해드려요~")
            .sellingLocation("SEOUL")
            .bookStatus("BEST")
            .price("5000")
            .build();
        Article article = Article.create(user, articleRequest.toEntity());

        when(articleRepository.findById(any())).thenReturn(Optional.ofNullable(article));
        when(likeRepository.save(any())).thenReturn(Like.create(user, article));

        likeService.addLike(user, new LikeRequest(1L));

        verify(articleRepository).findById(any());
        verify(likeRepository).save(any());
    }

    @DisplayName("잘못된 article_id 로 관심목록에 추가하면 예외가 발생한다.")
    @Test
    void addLike_invalidArticleId_failure() {
        when(articleRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> likeService.addLike(user, new LikeRequest(1L)))
            .isInstanceOf(CustomException.class)
            .hasMessage(ErrorCode.INVALID_ARTICLE_ID.getMessage());
    }

    @DisplayName("관심목록을 삭제한다.")
    @Test
    void deleteLike_success() {
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

        ArticleRequest articleRequest = ArticleRequest.builder()
            .bookRequest(bookRequest)
            .title("책 팝니다~")
            .description("쿨 거래시 1000원 할인해드려요~")
            .sellingLocation("SEOUL")
            .bookStatus("BEST")
            .price("5000")
            .build();
        Article article = Article.create(user, articleRequest.toEntity());

        when(likeRepository.findById(any())).thenReturn(Optional.of(Like.create(user, article)));
        doNothing().when(likeRepository).delete(any());

        likeService.deleteLike(user, 1L);

        verify(likeRepository).findById(any());
    }

    @DisplayName("권한이 없는 유저가 관심목록을 삭제하면 예외가 발생한다.")
    @Test
    void deleteLike_isNotMyLike_failure() {
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

        ArticleRequest articleRequest = ArticleRequest.builder()
            .bookRequest(bookRequest)
            .title("책 팝니다~")
            .description("쿨 거래시 1000원 할인해드려요~")
            .sellingLocation("SEOUL")
            .bookStatus("BEST")
            .price("5000")
            .build();
        Article article = Article.create(user, articleRequest.toEntity());
        User otherUser = User.builder().id(2L).build();

        when(likeRepository.findById(any())).thenReturn(
            Optional.of(Like.create(otherUser, article)));

        assertThatThrownBy(() -> likeService.deleteLike(user, 1L))
            .isInstanceOf(CustomException.class)
            .hasMessage(ErrorCode.USER_ACCESS_DENIED.getMessage());
    }

    @DisplayName("잘못된 like_id 로 관심목록을 삭제하면 예외가 발생한다.")
    @Test
    void deleteLike_invalidLikeId_failure() {
        when(likeRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> likeService.deleteLike(user, 1L))
            .isInstanceOf(CustomException.class)
            .hasMessage(ErrorCode.INVALID_LIKE_ID.getMessage());
    }
}