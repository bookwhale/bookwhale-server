package com.teamherb.bookstoreback.orders.dto;

import lombok.Builder;

import java.time.LocalDateTime;

public class SellHistory {

    private String bookTitle;

    private String author;

    private String publisher;

    private LocalDateTime sellDate;

    private Long price;

    private String purchaser;

    @Builder
    public SellHistory(String bookTitle, String author, String publisher, LocalDateTime sellDate, Long price,String purchaser) {
        this.bookTitle = bookTitle;
        this.author = author;
        this.publisher = publisher;
        this.sellDate = sellDate;
        this.price = price;
        this.purchaser = purchaser;
    }

    @Override
    public String toString() {
        return "SellHistory{" +
                "bookTitle='" + bookTitle + '\'' +
                ", author='" + author + '\'' +
                ", publisher='" + publisher + '\'' +
                ", sellDate=" + sellDate +
                ", price=" + price +
                ", purchaser='" + purchaser + '\'' +
                '}';
    }
}
