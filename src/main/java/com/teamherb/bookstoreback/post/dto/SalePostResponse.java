package com.teamherb.bookstoreback.post.dto;

import com.teamherb.bookstoreback.orders.domain.Orders;
import com.teamherb.bookstoreback.orders.dto.PurchaseOrder;
import com.teamherb.bookstoreback.post.domain.Post;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class SalePostResponse {

  private Long id;

  private String bookThumbnail;

  private String postTitle;

  private String bookTitle;

  private String bookPrice;

  private String postStatus;

  @Builder
  public SalePostResponse(Long id, String bookThumbnail, String postTitle, String bookTitle,
      String bookPrice, String postStatus) {
    this.id = id;
    this.bookThumbnail = bookThumbnail;
    this.postTitle = postTitle;
    this.bookTitle = bookTitle;
    this.bookPrice = bookPrice;
    this.postStatus = postStatus;
  }

  public static List<SalePostResponse> listOf(List<Post> posts) {
    return posts.stream().map(v ->
        SalePostResponse.builder()
            .id(v.getId())
            .bookThumbnail(v.getBook().getBookThumbnail())
            .postTitle(v.getTitle())
            .bookTitle(v.getBook().getBookTitle())
            .bookPrice(v.getPrice())
            .postStatus(v.getPostStatus().name())
            .build()
    ).collect(Collectors.toList());
  }
}
