package com.bookwhale.post.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class PostsRequest {

    private String title;
    private String author;
    private String publisher;
    private String sellingLocation;
    private String postStatus;

    @Builder
    public PostsRequest(String title, String author, String publisher, String sellingLocation,
        String postStatus) {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.sellingLocation = sellingLocation;
        this.postStatus = postStatus;
    }
}
