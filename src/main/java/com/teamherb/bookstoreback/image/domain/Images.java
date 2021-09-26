package com.teamherb.bookstoreback.image.domain;

import com.teamherb.bookstoreback.post.domain.Post;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Images {

  @BatchSize(size = 10)
  @OneToMany(mappedBy = "post", cascade = {CascadeType.PERSIST, CascadeType.REMOVE},
      orphanRemoval = true)
  private List<Image> images = new ArrayList<>();

  public static Images empty() {
    return new Images();
  }

  public void addImages(Post post, List<String> imagePaths) {
    for (String imagePath : imagePaths) {
      Image image = Image.createPostImage(post, imagePath);
      this.images.add(image);
    }
  }
}
