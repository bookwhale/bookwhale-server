package com.teamherb.bookstoreback.sale.dto;

import com.teamherb.bookstoreback.sale.domain.Sale;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SaleResponse {

    private String purchaserIdentity;

    private String purchaserName;

    private String postTitle;

    private String postPrice;

    private String bookTitle;

    private String bookThumbnail;

    private LocalDateTime createdDate;

    @Builder
    public SaleResponse(String purchaserIdentity, String purchaserName, String postTitle,
        String postPrice, String bookTitle, String bookThumbnail, LocalDateTime createdDate) {
        this.purchaserIdentity = purchaserIdentity;
        this.purchaserName = purchaserName;
        this.postTitle = postTitle;
        this.postPrice = postPrice;
        this.bookTitle = bookTitle;
        this.bookThumbnail = bookThumbnail;
        this.createdDate = createdDate;
    }

    public static List<SaleResponse> listOf(List<Sale> sales) {
        return sales.stream().map(v ->
            SaleResponse.builder()
                .purchaserIdentity(v.getPurchaserIdentity())
                .purchaserName(v.getPurchaserName())
                .bookTitle(v.getBookTitle())
                .bookThumbnail(v.getBookThumbnail())
                .postTitle(v.getPostTitle())
                .postPrice(v.getPostPrice())
                .createdDate(v.getCreatedDate())
                .build()
        ).collect(Collectors.toList());
    }
}



