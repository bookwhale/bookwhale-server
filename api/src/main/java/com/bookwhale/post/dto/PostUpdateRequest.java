package com.bookwhale.post.dto;

import com.bookwhale.common.validator.ValueOfEnum;
import com.bookwhale.post.domain.BookStatus;
import com.bookwhale.post.domain.Post;

import java.util.List;
import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PostUpdateRequest {

  @NotBlank
  private String title;

  @NotBlank
  private String price;

  @NotBlank
  private String description;

  @NotBlank
  @ValueOfEnum(enumClass = BookStatus.class)
  private String bookStatus;

  private List<String> deleteImgUrls;

  @Builder
  public PostUpdateRequest(String title, String price, String description, String bookStatus,
      List<String> deleteImgUrls) {
    this.title = title;
    this.price = price;
    this.description = description;
    this.bookStatus = bookStatus;
    this.deleteImgUrls = deleteImgUrls;
  }

  public Post toEntity() {
    return Post.builder()
        .title(title)
        .price(price)
        .description(description)
        .bookStatus(BookStatus.valueOf(bookStatus))
        .build();
  }
}
