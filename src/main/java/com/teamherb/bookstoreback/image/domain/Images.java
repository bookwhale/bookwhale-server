package com.teamherb.bookstoreback.image.domain;

import com.teamherb.bookstoreback.post.domain.Post;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
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
  @OneToMany(mappedBy = "post", cascade = CascadeType.PERSIST, orphanRemoval = true)
  private List<Image> images = new ArrayList<>();

  public static Images empty() {
    return new Images();
  }

  public int getSize() {
    return images.size();
  }

  public boolean isEmpty() {
    return images.isEmpty();
  }

  public void addAll(Post post, List<String> imageUrls) {
    //TODO : Bulk Insert
    for (String imagePath : imageUrls) {
      Image image = Image.createPostImage(post, imagePath);
      this.images.add(image);
    }
  }

  public void deleteAll() {
    //TODO : Bulk Deletes
    images.clear();
  }

  public void deleteImageUrls(List<String> deleteImgUrls) {
    List<Image> newImages = images.stream()
        .filter(image -> deleteImgUrls.stream().noneMatch(Predicate.isEqual(image.getUrl())))
        .collect(Collectors.toList());
    images.clear();
    images.addAll(newImages);
  }

  public String getFirstImageUrl() {
    return images.isEmpty() ? null : images.get(0).getUrl();
  }

  public List<String> getImageUrls() {
    return images.stream().map(Image::getUrl).collect(Collectors.toList());
  }
}
