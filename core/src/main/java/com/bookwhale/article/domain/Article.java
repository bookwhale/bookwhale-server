package com.bookwhale.article.domain;

import static com.bookwhale.article.domain.ArticleStatus.RESERVED;
import static com.bookwhale.article.domain.ArticleStatus.SOLD_OUT;

import com.bookwhale.common.domain.BaseEntity;
import com.bookwhale.common.domain.Location;
import com.bookwhale.common.exception.CustomException;
import com.bookwhale.common.exception.ErrorCode;
import com.bookwhale.image.domain.Images;
import com.bookwhale.user.domain.User;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
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

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Article extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", foreignKey = @ForeignKey(name = "seller_fk_article"))
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

    private Long favoriteCount = 0L;

    private Long viewCount = 0L;

    private Long chatCount = 0L;

    @Embedded
    private Book book;

    @Embedded
    private final Images images = Images.empty();

    @Builder
    public Article(Long id, User seller, String title, String price, String description,
        ArticleStatus articleStatus,
        BookStatus bookStatus, Location sellingLocation, Long favoriteCount, Long viewCount,
        Long chatCount,
        Book book) {
        this.id = id;
        this.seller = seller;
        this.title = title;
        this.price = price;
        this.description = description;
        this.articleStatus = articleStatus;
        this.bookStatus = bookStatus;
        this.sellingLocation = sellingLocation;
        this.favoriteCount = favoriteCount == null ? 0L : favoriteCount;
        this.viewCount = viewCount == null ? 0L : viewCount;
        this.chatCount = chatCount == null ? 0L : chatCount;
        this.book = book;
    }

    public Location getSellingLocation() {
        return sellingLocation;
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
            .sellingLocation(article.getSellingLocation())
            .favoriteCount(article.getFavoriteCount())
            .viewCount(article.getViewCount())
            .chatCount(article.getChatCount())
            .build();
    }

    public void update(Article article) {
        this.title = article.getTitle();
        this.price = article.getPrice();
        this.description = article.getDescription();
        this.bookStatus = article.getBookStatus();
        this.sellingLocation = article.getSellingLocation();
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

    public void increaseOneViewCount(User loginUser) {
        Long sellerId = seller.getId();
        Long loginUserId = loginUser.getId();
        if (!sellerId.equals(loginUserId)) {
            this.viewCount += 1L;
        }
    }

    public void increaseOneFavoriteCount() {
        this.favoriteCount += 1L;
    }

    public void decreaseOneFavoriteCount() {
        if (this.favoriteCount > 0L) {
            this.favoriteCount -= 1L;
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
            ", favoriteCount=" + favoriteCount +
            ", viewCount=" + viewCount +
            ", book=" + book +
            ", images=" + images +
            '}';
    }
}
