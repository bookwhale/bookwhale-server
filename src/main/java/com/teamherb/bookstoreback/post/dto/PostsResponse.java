package com.teamherb.bookstoreback.post.dto;

import com.teamherb.bookstoreback.post.domain.Post;
import com.teamherb.bookstoreback.post.domain.PostStatus;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PostsResponse {

  private Long postId;
  private String postImage;
  private String postTitle;
  private String postPrice;
  private String bookTitle;
  private PostStatus postStatus;
  private LocalDateTime createdDate;

  @Builder
  public PostsResponse(Long postId, String postImage, String postTitle, String postPrice,
      String bookTitle, PostStatus postStatus, LocalDateTime createdDate) {
    this.postId = postId;
    this.postImage = postImage;
    this.postTitle = postTitle;
    this.postPrice = postPrice;
    this.bookTitle = bookTitle;
    this.postStatus = postStatus;
    this.createdDate = createdDate;
  }

  public static PostsResponse of(Post post, String postImage) {
    return PostsResponse.builder()
        .postId(post.getId())
        .postImage(postImage)
        .postTitle(post.getTitle())
        .postPrice(post.getPrice())
        .bookTitle(post.getBook().getBookTitle())
        .postStatus(post.getPostStatus())
        .createdDate(post.getCreatedDate())
        .build();
  }
}
