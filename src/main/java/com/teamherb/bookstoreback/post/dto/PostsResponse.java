package com.teamherb.bookstoreback.post.dto;

import com.teamherb.bookstoreback.common.utils.TimeUtils;
import com.teamherb.bookstoreback.post.domain.Post;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
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
  private String postStatus;
  private String bookTitle;
  private String bookAuthor;
  private String bookPublisher;
  private String beforeTime;

  @Builder
  public PostsResponse(Long postId, String postImage, String postTitle, String postPrice,
      String postStatus, String bookTitle, String bookAuthor, String bookPublisher,
      String beforeTime) {
    this.postId = postId;
    this.postImage = postImage;
    this.postTitle = postTitle;
    this.postPrice = postPrice;
    this.postStatus = postStatus;
    this.bookTitle = bookTitle;
    this.bookAuthor = bookAuthor;
    this.bookPublisher = bookPublisher;
    this.beforeTime = beforeTime;
  }

  public static PostsResponse of(Post post, String postImage, LocalDateTime currentTime) {
    return PostsResponse.builder()
        .postId(post.getId())
        .postImage(postImage)
        .postTitle(post.getTitle())
        .postPrice(post.getPrice())
        .postStatus(post.getPostStatus().getName())
        .bookTitle(post.getBook().getBookTitle())
        .bookAuthor(post.getBook().getBookAuthor())
        .bookPublisher(post.getBook().getBookPublisher())
        .beforeTime(TimeUtils.BeforeTime(currentTime, post.getCreatedDate()))
        .build();
  }

  public static List<PostsResponse> listOf(List<Post> posts) {
    LocalDateTime cur = LocalDateTime.now();
    return posts.stream()
        .map(p -> PostsResponse.of(p, p.getImages().getFirstImageUrl(), cur))
        .collect(Collectors.toList());
  }
}
