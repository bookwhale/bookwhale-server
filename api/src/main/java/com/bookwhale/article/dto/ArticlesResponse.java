package com.bookwhale.article.dto;

import com.bookwhale.article.domain.Article;
import com.bookwhale.common.utils.TimeUtils;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ArticlesResponse {

    private Long articleId;
    private String articleImage;
    private String articleTitle;
    private String articlePrice;
    private String bookStatus;
    private String sellingLocation;
    private Long chatCount;
    private Long favoriteCount;
    private String beforeTime;

    @Builder
    public ArticlesResponse(Long articleId, String articleImage, String articleTitle, String articlePrice,
        String bookStatus, String sellingLocation, Long chatCount, Long favoriteCount, String beforeTime) {
        this.articleId = articleId;
        this.articleImage = articleImage;
        this.articleTitle = articleTitle;
        this.articlePrice = articlePrice;
        this.bookStatus = bookStatus;
        this.sellingLocation = sellingLocation;
        this.chatCount = chatCount;
        this.favoriteCount = favoriteCount;
        this.beforeTime = beforeTime;
    }

    public static ArticlesResponse of(Article article, String articleImage, LocalDateTime currentTime) {
        return ArticlesResponse.builder()
            .articleId(article.getId())
            .articleImage(articleImage)
            .articleTitle(article.getTitle())
            .articlePrice(article.getPrice())
            .bookStatus(article.getBookStatus().getName())
            .sellingLocation(article.getSellingLocation().getName())
            .favoriteCount(article.getFavoriteCount())
            .chatCount(article.getChatCount())
            .beforeTime(TimeUtils.BeforeTime(currentTime, article.getCreatedDate()))
            .build();
    }

    public static List<ArticlesResponse> listOf(List<Article> articles) {
        return articles.stream()
            .map(article -> ArticlesResponse.of(article, article.getImages().getFirstImageUrl(), LocalDateTime.now()))
            .collect(Collectors.toList());
    }
}
