package com.teamherb.bookstoreback.post.domain;

import com.teamherb.bookstoreback.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long>, PostCustomRepository {

  Page<Post> findAllBySeller(User user, Pageable pageable);
}
