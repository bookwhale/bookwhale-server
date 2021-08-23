package com.teamherb.bookstoreback.post.domain;

import com.teamherb.bookstoreback.post.dto.BookRequest;
import javax.persistence.Embeddable;
import javax.persistence.Lob;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

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

    public static Book create(BookRequest req) {
        return Book.builder()
            .bookIsbn(req.getBookIsbn())
            .bookTitle(req.getBookTitle())
            .bookAuthor(req.getBookAuthor())
            .bookPublisher(req.getBookPublisher())
            .bookThumbnail(req.getBookThumbnail())
            .bookListPrice(req.getBookListPrice())
            .bookPubDate(req.getBookPubDate())
            .bookSummary(req.getBookSummary())
            .build();
    }
}
