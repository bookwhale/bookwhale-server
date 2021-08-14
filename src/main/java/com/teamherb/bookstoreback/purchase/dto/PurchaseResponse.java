package com.teamherb.bookstoreback.purchase.dto;

import com.teamherb.bookstoreback.purchase.domain.Purchase;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PurchaseResponse {

    private String sellerIdentity;

    private String sellerName;

    private String postTitle;

    private String postPrice;

    private String bookTitle;

    private String bookThumbnail;

    private String createdDate;

    @Builder
    public PurchaseResponse(String sellerIdentity, String sellerName, String postTitle,
        String postPrice, String bookTitle, String bookThumbnail, String createdDate) {
        this.sellerIdentity = sellerIdentity;
        this.sellerName = sellerName;
        this.postTitle = postTitle;
        this.postPrice = postPrice;
        this.bookTitle = bookTitle;
        this.bookThumbnail = bookThumbnail;
        this.createdDate = createdDate;
    }

    public static List<PurchaseResponse> listOf(List<Purchase> purchases) {
        return purchases.stream().map(v ->
            PurchaseResponse.builder()
                .sellerIdentity(v.getSellerIdentity())
                .sellerName(v.getSellerName())
                .bookTitle(v.getBookTitle())
                .bookThumbnail(v.getBookThumbnail())
                .postTitle(v.getPostTitle())
                .postPrice(v.getPostPrice())
                .createdDate(String.valueOf(v.getCreatedDate()))
                .build()
        ).collect(Collectors.toList());
    }
}
