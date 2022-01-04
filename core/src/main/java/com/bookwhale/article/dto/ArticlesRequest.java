package com.bookwhale.article.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ArticlesRequest {

    private String search;

    @Builder
    public ArticlesRequest(String search) {
        this.search = search;
    }
}
