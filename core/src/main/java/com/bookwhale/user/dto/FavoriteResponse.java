package com.bookwhale.user.dto;

import com.bookwhale.article.domain.Article;
import com.bookwhale.article.dto.ArticlesResponse;
import com.bookwhale.favorite.domain.Favorite;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FavoriteResponse {

    private Long favoriteId;

    private ArticlesResponse articlesResponse;

    public FavoriteResponse(Long favoriteId,
        ArticlesResponse articlesResponse) {
        this.favoriteId = favoriteId;
        this.articlesResponse = articlesResponse;
    }

    public static FavoriteResponse of(Favorite favorite) {
        LocalDateTime cur = LocalDateTime.now();
        Article article = favorite.getArticle();

        return new FavoriteResponse(favorite.getId(),
            ArticlesResponse.of(article, article.getImages().getFirstImageUrl(), cur));
    }

    public static List<FavoriteResponse> listOf(List<Favorite> favorites) {
        LocalDateTime cur = LocalDateTime.now();
        return favorites.stream().map(favorite -> {
            Article article = favorite.getArticle();
            return new FavoriteResponse(favorite.getId(),
                ArticlesResponse.of(article, article.getImages().getFirstImageUrl(), cur));
        }).collect(Collectors.toList());
    }
}
