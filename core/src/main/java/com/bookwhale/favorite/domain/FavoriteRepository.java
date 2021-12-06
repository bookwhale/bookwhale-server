package com.bookwhale.favorite.domain;

import com.bookwhale.article.domain.Article;
import com.bookwhale.user.domain.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    @Query(value = "select i from Favorite i join fetch i.article where i.user = ?1")
    List<Favorite> findAllByUser(User user);

    boolean existsByUserAndArticle(User user, Article article);
}
