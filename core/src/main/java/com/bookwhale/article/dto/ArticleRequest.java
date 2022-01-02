package com.bookwhale.article.dto;

import com.bookwhale.article.domain.Article;
import com.bookwhale.article.domain.ArticleStatus;
import com.bookwhale.article.domain.Book;
import com.bookwhale.article.domain.BookStatus;
import com.bookwhale.common.domain.Location;
import com.bookwhale.common.validator.ValueOfEnum;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ArticleRequest {

    @Valid
    BookRequest bookRequest;

    @NotBlank
    private String title;

    @NotBlank
    private String price;

    @NotBlank
    private String description;

    @NotBlank
    @ValueOfEnum(enumClass = BookStatus.class)
    private String bookStatus;

    @NotBlank
    @ValueOfEnum(enumClass = Location.class)
    private String sellingLocation;

    @Builder
    public ArticleRequest(BookRequest bookRequest, String title, String price,
        String description, String bookStatus, String sellingLocation) {
        this.bookRequest = bookRequest;
        this.title = title;
        this.price = price;
        this.description = description;
        this.bookStatus = bookStatus;
        this.sellingLocation = sellingLocation;
    }

    public Article toEntity() {
        return Article.builder()
            .title(title)
            .price(price)
            .articleStatus(ArticleStatus.SALE)
            .bookStatus(BookStatus.valueOf(bookStatus))
            .sellingLocation(Location.valueOf(sellingLocation))
            .description(description)
            .book(Book.create(bookRequest.toEntity()))
            .build();
    }
}