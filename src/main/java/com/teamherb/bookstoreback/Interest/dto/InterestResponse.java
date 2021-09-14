package com.teamherb.bookstoreback.Interest.dto;

import com.teamherb.bookstoreback.Interest.domain.Interest;
import com.teamherb.bookstoreback.post.domain.PostStatus;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class InterestResponse {

  private Long interestId;

  private Long postId;

  private String bookThumbnail;

  private String postTitle;

  private String bookTitle;

  private String postPrice;

  private PostStatus postStatus;

  @Builder
  public InterestResponse(Long interestId, Long postId, String bookThumbnail,
      String postTitle, String bookTitle, String postPrice, PostStatus postStatus) {
    this.interestId = interestId;
    this.postId = postId;
    this.bookThumbnail = bookThumbnail;
    this.postTitle = postTitle;
    this.bookTitle = bookTitle;
    this.postPrice = postPrice;
    this.postStatus = postStatus;
  }

  public static List<InterestResponse> listOf(List<Interest> interests) {
    return interests.stream().map(v -> InterestResponse.builder()
        .interestId(v.getId())
        .postId(v.getPost().getId())
        .bookThumbnail(v.getPost().getBook().getBookThumbnail())
        .postTitle(v.getPost().getTitle())
        .bookTitle(v.getPost().getBook().getBookTitle())
        .postPrice(v.getPost().getPrice())
        .postStatus(v.getPost().getPostStatus())
        .build()
    ).collect(Collectors.toList());
  }
}
