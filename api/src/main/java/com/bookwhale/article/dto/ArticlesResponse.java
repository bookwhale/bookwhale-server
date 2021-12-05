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
    private String articleStatus;
    private String description;
    private String sellingLocation;
    private Long viewCount;
    private Long favoriteCount;
    private String beforeTime;

    @Builder
    public ArticlesResponse(Long articleId, String articleImage, String articleTitle,
        String articlePrice, String articleStatus, String description, String bookTitle,
        String bookAuthor, String bookPublisher, String sellingLocation, Long viewCount,
        Long favoriteCount, String beforeTime) {
        this.articleId = articleId;
        this.articleImage = articleImage;
        this.articleTitle = articleTitle;
        this.articlePrice = articlePrice;
        this.articleStatus = articleStatus;
        this.description = description;
        this.sellingLocation = sellingLocation;
        this.viewCount = viewCount;
        this.favoriteCount = favoriteCount;
        this.beforeTime = beforeTime;
    }



    public static ArticlesResponse of(Article article, String articleImage, LocalDateTime currentTime) {
        return ArticlesResponse.builder()
            .articleId(article.getId())
            .articleImage(articleImage)
            .articleTitle(article.getTitle())
            .articlePrice(article.getPrice())
            .articleStatus(article.getArticleStatus().getName())
            .description(article.getDescription())
            .sellingLocation(article.getSellingLocation().getName())
            .viewCount(article.getViewCount())
            .favoriteCount(article.getFavoriteCount())
            .beforeTime(TimeUtils.BeforeTime(currentTime, article.getCreatedDate()))
            .build();
    }

    public static List<ArticlesResponse> listOf(List<Article> articles) {
        LocalDateTime cur = LocalDateTime.now();
        return articles.stream()
            .map(p -> ArticlesResponse.of(p, p.getImages().getFirstImageUrl(), cur))
            .collect(Collectors.toList());
    }
}
