package com.teamherb.bookstoreback.post.dto;

import com.teamherb.bookstoreback.account.dto.AccountRequest;
import com.teamherb.bookstoreback.common.validator.ValueOfEnum;
import com.teamherb.bookstoreback.post.domain.BookStatus;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PostRequest {

    @Valid
    AccountRequest accountRequest;

    @Valid
    BookRequest bookRequest;

    @NotBlank
    private String title;

    @NotBlank
    private String price;

    @NotBlank
    private String description;

    @ValueOfEnum(enumClass = BookStatus.class)
    private String bookStatus;

    @Builder
    public PostRequest(AccountRequest accountRequest,
        BookRequest bookRequest, String title, String price, String description,
        String bookStatus) {
        this.accountRequest = accountRequest;
        this.bookRequest = bookRequest;
        this.title = title;
        this.price = price;
        this.description = description;
        this.bookStatus = bookStatus;
    }
}
