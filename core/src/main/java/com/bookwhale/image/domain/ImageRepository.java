package com.bookwhale.image.domain;

import com.bookwhale.post.domain.Post;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {

    List<Image> findAllByPost(Post post);
}
