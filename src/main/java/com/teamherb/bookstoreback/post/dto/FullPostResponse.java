package com.teamherb.bookstoreback.post.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.teamherb.bookstoreback.post.domain.PostStatus;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FullPostResponse {

    private Long postId;
    private String bookThumbnail;
    private String postTitle;
    private String postPrice;
    private String bookTitle;
    private PostStatus postStatus;
    private LocalDateTime createdDate;

    @Builder
    @QueryProjection
    public FullPostResponse(Long postId, String bookThumbnail, String postTitle, String postPrice,
        String bookTitle, PostStatus postStatus, LocalDateTime createdDate) {
        this.postId = postId;
        this.bookThumbnail = bookThumbnail;
        this.postTitle = postTitle;
        this.postPrice = postPrice;
        this.bookTitle = bookTitle;
        this.postStatus = postStatus;
        this.createdDate = createdDate;
    }
}
