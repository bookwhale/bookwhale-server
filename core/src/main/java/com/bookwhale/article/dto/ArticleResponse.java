package com.bookwhale.article.dto;

import com.bookwhale.article.domain.Article;
import com.bookwhale.common.utils.TimeUtils;
import com.bookwhale.image.domain.Image;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ArticleResponse {

    private Long sellerId;
    private String sellerIdentity;
    private String sellerProfileImage;
    private Long articleId;
    private String title;
    private String price;
    private String description;
    private String bookStatus;
    private String articleStatus;
    private String sellingLocation;
    private List<String> images;
    private BookResponse bookResponse;
    private boolean isMyArticle;
    private boolean isMyFavorite;
    private Long myFavoriteId;
    private Long viewCount;
    private Long favoriteCount;
    private String beforeTime;

    @Builder
    public ArticleResponse(Long sellerId, String sellerIdentity, String sellerProfileImage,
        Long articleId, String title, String price, String description, String bookStatus,
        String articleStatus, String sellingLocation, List<String> images,
        BookResponse bookResponse, boolean isMyArticle, boolean isMyFavorite,
        Long myFavoriteId, Long viewCount, Long favoriteCount, String beforeTime) {
        this.sellerId = sellerId;
        this.sellerIdentity = sellerIdentity;
        this.sellerProfileImage = sellerProfileImage;
        this.articleId = articleId;
        this.title = title;
        this.price = price;
        this.description = description;
        this.bookStatus = bookStatus;
        this.articleStatus = articleStatus;
        this.sellingLocation = sellingLocation;
        this.images = images;
        this.bookResponse = bookResponse;
        this.isMyArticle = isMyArticle;
        this.isMyFavorite = isMyFavorite;
        this.myFavoriteId = myFavoriteId;
        this.viewCount = viewCount;
        this.favoriteCount = favoriteCount;
        this.beforeTime = beforeTime;
    }



    public static ArticleResponse of(Article article, boolean isMyArticle,
        boolean isMyFavorite, Long MyFavoriteId) {
        BookResponse bookResponse = BookResponse.builder()
            .bookAuthor(article.getBook().getBookAuthor())
            .bookIsbn(article.getBook().getBookIsbn())
            .bookListPrice(article.getBook().getBookListPrice())
            .bookPubDate(article.getBook().getBookPubDate())
            .bookPublisher(article.getBook().getBookPublisher())
            .bookSummary(article.getBook().getBookSummary())
            .bookThumbnail(article.getBook().getBookThumbnail())
            .bookTitle(article.getBook().getBookTitle())
            .build();

        List<String> imageResponse = article.getImages().getImages().stream().map(Image::getUrl)
            .collect(Collectors.toList());

        return ArticleResponse.builder()
            .bookResponse(bookResponse)
            .sellerId(article.getSeller().getId())
            .sellerIdentity(article.getSeller().getNickname())
            .sellerProfileImage(article.getSeller().getProfileImage())
            .articleId(article.getId())
            .title(article.getTitle())
            .price(article.getPrice())
            .description(article.getDescription())
            .bookStatus(article.getBookStatus().getName())
            .articleStatus(article.getArticleStatus().getName())
            .sellingLocation(article.getSellingLocation().getName())
            .images(imageResponse)
            .isMyArticle(isMyArticle)
            .isMyFavorite(isMyFavorite)
            .myFavoriteId(MyFavoriteId)
            .viewCount(article.getViewCount())
            .favoriteCount(article.getFavoriteCount())
            .beforeTime(TimeUtils.BeforeTime(LocalDateTime.now(), article.getCreatedDate()))
            .build();
    }
}
