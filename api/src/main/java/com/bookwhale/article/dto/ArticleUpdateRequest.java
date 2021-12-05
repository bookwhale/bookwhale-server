package com.bookwhale.article.dto;

import com.bookwhale.article.domain.Article;
import com.bookwhale.article.domain.BookStatus;
import com.bookwhale.common.domain.Location;
import com.bookwhale.common.validator.ValueOfEnum;
import java.util.List;
import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ArticleUpdateRequest {

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

    private List<String> deleteImgUrls;

    @Builder
    public ArticleUpdateRequest(String title, String price, String description, String bookStatus,
        String sellingLocation, List<String> deleteImgUrls) {
        this.title = title;
        this.price = price;
        this.description = description;
        this.bookStatus = bookStatus;
        this.sellingLocation = sellingLocation;
        this.deleteImgUrls = deleteImgUrls;
    }

    public Article toEntity() {
        return Article.builder()
            .title(title)
            .price(price)
            .description(description)
            .bookStatus(BookStatus.valueOf(bookStatus))
            .sellingLocation(Location.valueOf(sellingLocation))
            .build();
    }
}
