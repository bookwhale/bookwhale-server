package com.teamherb.bookstoreback.image.domain;

import com.teamherb.bookstoreback.post.domain.Post;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Image {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "image_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "post_id")
  private Post post;

  private String path;

  private Image(Post post, String path) {
    this.post = post;
    this.path = path;
  }

  public static List<Image> createPostImage(Post post, List<String> uploadFilePaths) {
    return uploadFilePaths.stream().map(path -> new Image(post, path))
        .collect(Collectors.toList());
  }
}
