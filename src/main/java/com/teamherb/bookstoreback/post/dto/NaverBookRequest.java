package com.teamherb.bookstoreback.post.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NaverBookRequest {

    private String title;
    private String isbn;
    private String author;

    @Builder
    public NaverBookRequest(String isbn, String title, String author) {
        this.title = title;
        this.isbn = isbn;
        this.author = author;
    }
}
