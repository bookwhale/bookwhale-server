package com.bookwhale.post.domain;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostCustomRepository {

    Optional<Post> findPostWithSellerById(Long id);

    Page<Post> findAllOrderByCreatedDateDesc(String title, String author, String publisher,
        String sellingLocation, String postStatus, Pageable pageable);
}
