package com.bookwhale.post.domain;

import javax.persistence.Embeddable;
import javax.persistence.Lob;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Book {

  private String bookIsbn;

  private String bookTitle;

  private String bookAuthor;

  private String bookPublisher;

  private String bookThumbnail;

  private String bookListPrice;

  private String bookPubDate;

  @Lob
  private String bookSummary;

  @Builder
  public Book(String bookIsbn, String bookTitle, String bookAuthor, String bookPublisher,
      String bookThumbnail, String bookListPrice, String bookPubDate, String bookSummary) {
    this.bookIsbn = bookIsbn;
    this.bookTitle = bookTitle;
    this.bookAuthor = bookAuthor;
    this.bookPublisher = bookPublisher;
    this.bookThumbnail = bookThumbnail;
    this.bookListPrice = bookListPrice;
    this.bookPubDate = bookPubDate;
    this.bookSummary = bookSummary;
  }

  public static Book create(Book book) {
    return Book.builder()
        .bookIsbn(book.getBookIsbn())
        .bookTitle(book.getBookTitle())
        .bookAuthor(book.getBookAuthor())
        .bookPublisher(book.getBookPublisher())
        .bookThumbnail(book.getBookThumbnail())
        .bookListPrice(book.getBookListPrice())
        .bookPubDate(book.getBookPubDate())
        .bookSummary(book.getBookSummary())
        .build();
  }
}
