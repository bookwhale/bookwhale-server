package com.teamherb.bookstoreback.post.dto;

import com.teamherb.bookstoreback.common.validator.ValueOfEnum;
import com.teamherb.bookstoreback.post.domain.BookStatus;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PostUpdateRequest {

  @NotNull
  private Long postId;

  @NotBlank
  private String title;

  @NotBlank
  private String price;

  @NotBlank
  private String description;

  @NotBlank
  @ValueOfEnum(enumClass = BookStatus.class)
  private String bookStatus;

  @Builder
  public PostUpdateRequest(Long postId, String title, String price, String description,
      String bookStatus) {
    this.postId = postId;
    this.title = title;
    this.price = price;
    this.description = description;
    this.bookStatus = bookStatus;
  }
}
