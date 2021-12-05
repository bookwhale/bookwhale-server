package com.bookwhale.user.dto;

import com.bookwhale.article.domain.Article;
import com.bookwhale.like.domain.Like;
import com.bookwhale.article.dto.ArticlesResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LikeResponse {

    private Long likeId;

    private ArticlesResponse articlesResponse;

    public LikeResponse(Long likeId,
        ArticlesResponse articlesResponse) {
        this.likeId = likeId;
        this.articlesResponse = articlesResponse;
    }

    public static List<LikeResponse> listOf(List<Like> likes) {
        LocalDateTime cur = LocalDateTime.now();
        return likes.stream().map(likedArticle -> {
            Article article = likedArticle.getArticle();
            return new LikeResponse(likedArticle.getId(),
                ArticlesResponse.of(article, article.getImages().getFirstImageUrl(), cur));
        }).collect(Collectors.toList());
    }
}
