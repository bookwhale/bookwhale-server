package com.teamherb.bookstoreback.Interest.domain;

import com.teamherb.bookstoreback.post.domain.Post;
import com.teamherb.bookstoreback.user.domain.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InterestRepository extends JpaRepository<Interest, Long> {

  @Query(value = "select i from Interest i join fetch i.post where i.user = ?1")
  List<Interest> findAllByUser(User user);

  boolean existsByUserAndPost(User user, Post post);
}
