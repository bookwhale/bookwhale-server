package com.teamherb.bookstoreback.post.domain;

import java.time.LocalDateTime;
import javax.persistence.Embeddable;
import javax.persistence.Lob;

@Embeddable
public class Book {

    private Long bookIsbn;

    private String bookTitle;

    private String bookAuthor;

    private String bookPublisher;

    private String bookCategory;

    private String bookThumbnail;

    private Long bookListPrice;

    private LocalDateTime bookPubDate;

    @Lob
    private String bookSummary;
}
