package com.bookwhale.post.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PostsRequest {

  private String title;
  private String author;
  private String publisher;

  @Builder
  public PostsRequest(String title, String author, String publisher) {
    this.title = title;
    this.author = author;
    this.publisher = publisher;
  }
}
