package com.teamherb.bookstoreback.orders.dto;

import lombok.Builder;

import java.time.LocalDateTime;

public class PurchaseHistory {

    private String bookTitle;

    private String author;

    private String publisher;

    private String deliveryPlace;

    private LocalDateTime purchaseDate;

    private String seller;

    private Long price;

    @Builder
    public PurchaseHistory(String bookTitle, String author, String publisher, String deliveryPlace, LocalDateTime purchaseDate, Long price, String seller) {
        this.bookTitle = bookTitle;
        this.author = author;
        this.publisher = publisher;
        this.deliveryPlace = deliveryPlace;
        this.purchaseDate = purchaseDate;
        this.price = price;
        this.seller = seller;
    }

    @Override
    public String toString() {
        return "PurchaseHistory{" +
                "bookTitle='" + bookTitle + '\'' +
                ", author='" + author + '\'' +
                ", publisher='" + publisher + '\'' +
                ", deliveryPlace='" + deliveryPlace + '\'' +
                ", purchaseDate=" + purchaseDate +
                ", seller='" + seller + '\'' +
                ", price=" + price +
                '}';
    }
}
