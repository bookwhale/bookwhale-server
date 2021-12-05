package com.bookwhale.like.domain;

import com.bookwhale.article.domain.Article;
import com.bookwhale.user.domain.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LikeRepository extends JpaRepository<Like, Long> {

    @Query(value = "select i from Like i join fetch i.article where i.user = ?1")
    List<Like> findAllByUser(User user);

    boolean existsByUserAndArticle(User user, Article article);
}
