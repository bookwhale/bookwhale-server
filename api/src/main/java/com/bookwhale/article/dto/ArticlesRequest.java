package com.bookwhale.article.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ArticlesRequest {

    private String title;
    private String author;
    private String publisher;
    private String sellingLocation;
    private String articleStatus;

    @Builder
    public ArticlesRequest(String title, String author, String publisher, String sellingLocation,
        String articleStatus) {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.sellingLocation = sellingLocation;
        this.articleStatus = articleStatus;
    }
}
