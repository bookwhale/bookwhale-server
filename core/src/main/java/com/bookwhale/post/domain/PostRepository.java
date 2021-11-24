package com.bookwhale.post.domain;

import com.bookwhale.user.domain.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long>, PostCustomRepository {

    List<Post> findAllBySeller(User user);
}
