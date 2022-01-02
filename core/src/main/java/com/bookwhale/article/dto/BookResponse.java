package com.bookwhale.article.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BookResponse {

    private String bookIsbn;
    private String bookTitle;
    private String bookAuthor;
    private String bookPublisher;
    private String bookThumbnail;
    private String bookListPrice;
    private String bookPubDate;
    private String bookSummary;

    @Builder
    public BookResponse(String bookIsbn, String bookTitle, String bookAuthor,
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
}
