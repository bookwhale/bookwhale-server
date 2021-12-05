package com.bookwhale.article.domain;

import static com.bookwhale.article.domain.ArticleStatus.RESERVED;
import static com.bookwhale.article.domain.ArticleStatus.SOLD_OUT;

import com.bookwhale.common.domain.BaseEntity;
import com.bookwhale.common.domain.Location;
import com.bookwhale.common.exception.CustomException;
import com.bookwhale.common.exception.ErrorCode;
import com.bookwhale.image.domain.Images;
import com.bookwhale.user.domain.User;
import java.util.Optional;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Article extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "article_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private User seller;

    private String title;

    private String price;

    @Lob
    private String description;

    @Enumerated(EnumType.STRING)
    private ArticleStatus articleStatus;

    @Enumerated(EnumType.STRING)
    private BookStatus bookStatus;

    @Enumerated(EnumType.STRING)
    private Location sellingLocation;

    private Long likeCount = 0L;

    private Long viewCount = 0L;

    @Embedded
    private Book book;

    @Embedded
    private final Images images = Images.empty();

    @Builder
    public Article(Long id, User seller, String title, String price, String description,
        ArticleStatus articleStatus, BookStatus bookStatus,
        Location sellingLocation, Long likeCount, Long viewCount, Book book) {
        this.id = id;
        this.seller = seller;
        this.title = title;
        this.price = price;
        this.description = description;
        this.articleStatus = articleStatus;
        this.bookStatus = bookStatus;
        this.sellingLocation = sellingLocation;
        this.likeCount = likeCount == null ? 0L : likeCount;
        this.viewCount = viewCount == null ? 0L : viewCount;
        this.book = book;
    }

    public Optional<Location> getSellingLocation() {
        return Optional.ofNullable(sellingLocation);
    }

    public static Article create(User loginUser, Article article) {
        return Article.builder()
            .seller(loginUser)
            .title(article.getTitle())
            .price(article.getPrice())
            .articleStatus(ArticleStatus.SALE)
            .bookStatus(article.getBookStatus())
            .description(article.getDescription())
            .book(Book.create(article.getBook()))
            .sellingLocation(article.getSellingLocation().orElse(null))
            .likeCount(article.getLikeCount())
            .viewCount(article.getViewCount())
            .build();
    }

    public void update(Article article) {
        this.title = article.getTitle();
        this.price = article.getPrice();
        this.description = article.getDescription();
        this.bookStatus = article.getBookStatus();
    }

    public void validateIsMyArticle(User loginUser) {
        if (!this.isMyArticle(loginUser)) {
            throw new CustomException(ErrorCode.USER_ACCESS_DENIED);
        }
    }

    public boolean isMyArticle(User loginUser) {
        return this.seller.getId().equals(loginUser.getId());
    }

    public void updateArticleStatus(String articleStatus) {
        this.articleStatus = ArticleStatus.valueOf(articleStatus);
    }

    public void validateArticleStatus() {
        if (this.articleStatus.equals(RESERVED) || this.articleStatus.equals(SOLD_OUT)) {
            throw new CustomException(ErrorCode.INVALID_ARTICLE_STATUS);
        }
    }

    public void increaseOneViewCount() {
        this.viewCount += 1L;
    }

    public void increaseOneLikeCount() {
        this.likeCount += 1L;
    }

    public void decreaseOneLikeCount() {
        if (this.likeCount > 0L) {
            this.likeCount -= 1L;
        }
    }

    @Override
    public String toString() {
        return "Article{" +
            "id=" + id +
            ", seller=" + seller +
            ", title='" + title + '\'' +
            ", price='" + price + '\'' +
            ", description='" + description + '\'' +
            ", articleStatus=" + articleStatus +
            ", bookStatus=" + bookStatus +
            ", sellingLocation=" + sellingLocation +
            ", likeCount=" + likeCount +
            ", viewCount=" + viewCount +
            ", book=" + book +
            ", images=" + images +
            '}';
    }
}
