package com.bookwhale.article.domain;

import com.bookwhale.user.domain.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Long>, ArticleCustomRepository {

    List<Article> findAllBySeller(User user);
}
