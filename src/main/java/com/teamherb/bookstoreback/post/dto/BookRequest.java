package com.teamherb.bookstoreback.post.dto;

import com.teamherb.bookstoreback.post.domain.Book;
import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BookRequest {

  @NotBlank
  private String bookIsbn;

  @NotBlank
  private String bookTitle;

  @NotBlank
  private String bookAuthor;

  @NotBlank
  private String bookPublisher;

  @NotBlank
  private String bookThumbnail;

  @NotBlank
  private String bookListPrice;

  @NotBlank
  private String bookPubDate;

  @NotBlank
  private String bookSummary;

  @Builder
  public BookRequest(String bookIsbn, String bookTitle, String bookAuthor,
      String bookPublisher, String bookThumbnail, String bookListPrice, String bookPubDate,
      String bookSummary) {
    this.bookIsbn = bookIsbn;
    this.bookTitle = bookTitle;
    this.bookAuthor = bookAuthor;
    this.bookPublisher = bookPublisher;
    this.bookThumbnail = bookThumbnail;
    this.bookListPrice = bookListPrice;
    this.bookPubDate = bookPubDate;
    this.bookSummary = bookSummary;
  }

  public Book toEntity() {
    return Book.builder()
        .bookIsbn(bookIsbn)
        .bookTitle(bookTitle)
        .bookAuthor(bookAuthor)
        .bookPublisher(bookPublisher)
        .bookThumbnail(bookThumbnail)
        .bookListPrice(bookListPrice)
        .bookPubDate(bookPubDate)
        .bookSummary(bookSummary)
        .build();
  }
}
