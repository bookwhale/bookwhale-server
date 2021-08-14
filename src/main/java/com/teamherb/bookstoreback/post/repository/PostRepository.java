package com.teamherb.bookstoreback.post.repository;

import com.teamherb.bookstoreback.post.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post,Long> {
}
