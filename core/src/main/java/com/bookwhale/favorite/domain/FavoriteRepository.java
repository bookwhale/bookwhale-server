package com.bookwhale.favorite.domain;

import com.bookwhale.article.domain.Article;
import com.bookwhale.user.domain.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    @Query(value = "select i from Favorite i join fetch i.article where i.user = ?1 and i.article.activeYn = 'Y'")
    List<Favorite> findAllByUser(User user);

    boolean existsByUserAndArticle(User user, Article article);
    Optional<Favorite> findByUserAndArticle(User user, Article article);
}
