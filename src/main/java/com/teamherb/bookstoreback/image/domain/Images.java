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

  public void addAll(Post post, List<String> imagePaths) {
    //TODO : Bulk Insert
    for (String imagePath : imagePaths) {
      Image image = Image.createPostImage(post, imagePath);
      this.images.add(image);
    }
  }

  public void deleteAll() {
    //TODO : Bulk Deletes
    images.clear();
  }

  /*
  사용자가 게시글에 책 이미지를 등록하지 않았을 경우 대표 이미지는 null 로 반환한다.
  사용자가 게시글에 책 이미지를 등록한 경우 대표 이미지는 첫 번째 이미지를 반환한다.
  */
  public String getFirstImage() {
    return images.isEmpty() ? null : images.get(0).getPath();
  }

  public int getSize() {
    return images.size();
  }

  public boolean isEmpty() {
    return images.isEmpty();
  }
}
