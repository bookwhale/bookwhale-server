package com.teamherb.bookstoreback.post.domain;

import com.teamherb.bookstoreback.post.dto.PostsRequest;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostCustomRepository {

  Optional<Post> findPostWithSellerById(Long id);

  Page<Post> findAllByPostsReqOrderByCreatedDateDesc(PostsRequest req,
      Pageable pageable);
}
