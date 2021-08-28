package com.teamherb.bookstoreback.post.domain;

import com.teamherb.bookstoreback.post.dto.FullPostRequest;
import com.teamherb.bookstoreback.post.dto.FullPostResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostCustomRepository {

    Page<FullPostResponse> findAllByFullPostReqOrderByCreatedDateDesc(FullPostRequest req,
        Pageable pageable);
}
