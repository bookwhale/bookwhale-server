package com.bookwhale.post.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NaverBookRequest {

  private String title;
  private String isbn;
  private String author;

  @NotNull
  @Min(value = 10)
  @Max(value = 100)
  private Integer display;

  @NotNull
  @Min(value = 1)
  @Max(value = 1000)
  private Integer start;

  @Builder
  public NaverBookRequest(String title, String isbn, String author, Integer display,
      Integer start) {
    this.title = title;
    this.isbn = isbn;
    this.author = author;
    this.display = display;
    this.start = start;
  }
}
