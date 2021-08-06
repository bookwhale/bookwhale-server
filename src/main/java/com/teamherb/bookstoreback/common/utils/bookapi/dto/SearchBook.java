package com.teamherb.bookstoreback.common.utils.bookapi.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SearchBook {

    private String title;
    private String author;
    private String isbn;
    private String publisher;
    private String pubdate;
    private String price;
    private String description;
    private String image;
}
