package com.bookwhale.post.dto;

import com.bookwhale.common.validator.ValueOfEnum;
import com.bookwhale.post.domain.Book;
import com.bookwhale.post.domain.BookStatus;
import com.bookwhale.post.domain.Post;
import com.bookwhale.post.domain.PostStatus;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PostRequest {

  @Valid
  BookRequest bookRequest;

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
  public PostRequest(BookRequest bookRequest, String title, String price, String description,
      String bookStatus) {
    this.bookRequest = bookRequest;
    this.title = title;
    this.price = price;
    this.description = description;
    this.bookStatus = bookStatus;
  }

  public Post toEntity() {
    return Post.builder()
        .title(title)
        .price(price)
        .postStatus(PostStatus.SALE)
        .bookStatus(BookStatus.valueOf(bookStatus))
        .description(description)
        .book(Book.create(bookRequest.toEntity()))
        .build();
  }
}