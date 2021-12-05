package com.bookwhale.article.domain;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ArticleCustomRepository {

    Optional<Article> findArticleWithSellerById(Long id);

    Page<Article> findAllOrderByCreatedDateDesc(String title, String author, String publisher,
        Pageable pageable);
}
