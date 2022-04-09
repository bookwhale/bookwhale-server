package com.bookwhale.article.domain;

import com.bookwhale.user.domain.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;

public interface ArticleCustomRepository {

    Optional<Article> findArticleWithSellerById(Long id);

    List<Article> findAllBySearch(String search, Pageable pageable);

    List<Article> findAllBySeller(User user);
}
