package com.teamherb.bookstoreback.post.domain;

import com.teamherb.bookstoreback.post.dto.FullPostRequest;
import com.teamherb.bookstoreback.post.dto.FullPostResponse;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostCustomRepository {

  Optional<Post> findWithSellerById(Long id);

  Page<FullPostResponse> findAllByFullPostReqOrderByCreatedDateDesc(FullPostRequest req,
      Pageable pageable);
}
