package com.bookwhale.Interest.domain;

import com.bookwhale.post.domain.Post;
import com.bookwhale.user.domain.User;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface InterestRepository extends JpaRepository<Interest, Long> {

  @Query(value = "select i from Interest i join fetch i.post where i.user = ?1")
  List<Interest> findAllByUser(User user);

  boolean existsByUserAndPost(User user, Post post);
}
