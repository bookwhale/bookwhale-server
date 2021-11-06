package com.bookwhale.like.domain;

import com.bookwhale.post.domain.Post;
import com.bookwhale.user.domain.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface likeRepository extends JpaRepository<Like, Long> {

  @Query(value = "select i from Like i join fetch i.post where i.user = ?1")
  List<Like> findAllByUser(User user);

  boolean existsByUserAndPost(User user, Post post);
}
