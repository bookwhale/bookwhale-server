package com.bookwhale.image.domain;

import com.bookwhale.article.domain.Article;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {

    List<Image> findAllByArticle(Article article);
}
