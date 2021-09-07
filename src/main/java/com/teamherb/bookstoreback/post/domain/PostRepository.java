package com.teamherb.bookstoreback.post.domain;

import com.teamherb.bookstoreback.user.domain.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long>, PostCustomRepository {

  List<Post> findAllBySellerOrderByCreatedDate(User user);

}
