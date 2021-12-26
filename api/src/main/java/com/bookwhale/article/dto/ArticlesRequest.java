package com.bookwhale.article.dto;

import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ArticlesRequest {

    @NotBlank
    private String search;

    @Builder
    public ArticlesRequest(String search) {
        this.search = search;
    }
}
