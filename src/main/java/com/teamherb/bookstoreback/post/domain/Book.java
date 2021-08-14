package com.teamherb.bookstoreback.post.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import javax.persistence.Embeddable;
import javax.persistence.Lob;

@Embeddable
@Getter
public class Book {

    private Long bookIsbn;

    private String bookTitle;

    private String bookAuthor;

    private String bookPublisher;

    private String bookCategory;

    private String bookThumbnail;

    private Long bookPrice;

    private LocalDateTime bookPubDate;

    @Lob
    private String bookSummary;

    @Builder
    public Book(Long bookIsbn, String bookTitle, String bookAuthor, String bookPublisher, String bookCategory, String bookThumbnail, Long bookPrice, LocalDateTime bookPubDate, String bookSummary) {
        this.bookIsbn = bookIsbn;
        this.bookTitle = bookTitle;
        this.bookAuthor = bookAuthor;
        this.bookPublisher = bookPublisher;
        this.bookCategory = bookCategory;
        this.bookThumbnail = bookThumbnail;
        this.bookPrice = bookPrice;
        this.bookPubDate = bookPubDate;
        this.bookSummary = bookSummary;
    }

    public Book() {

    }
}
