package com.teamherb.bookstoreback.Interest.domain;

import com.teamherb.bookstoreback.post.domain.Post;
import com.teamherb.bookstoreback.user.domain.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterestRepository extends JpaRepository<Interest, Long> {

  List<Interest> findAllByUser(User user);

  boolean existsByUserAndPost(User user, Post post);
}
