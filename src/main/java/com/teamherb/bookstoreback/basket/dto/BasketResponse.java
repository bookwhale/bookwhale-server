package com.teamherb.bookstoreback.basket.dto;

import com.teamherb.bookstoreback.basket.domain.Basket;
import com.teamherb.bookstoreback.post.domain.Post;
import com.teamherb.bookstoreback.post.dto.SalePostResponse;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BasketResponse {

  private Long id;

  private String bookThumbnail;

  private String postTitle;

  private String bookTitle;

  private String bookPrice;

  private String sellerIdentity;

  private String postStatus;

  @Builder
  public BasketResponse(Long id, String bookThumbnail, String postTitle, String bookTitle,
      String bookPrice, String sellerIdentity, String postStatus) {
    this.id = id;
    this.bookThumbnail = bookThumbnail;
    this.postTitle = postTitle;
    this.bookTitle = bookTitle;
    this.bookPrice = bookPrice;
    this.sellerIdentity = sellerIdentity;
    this.postStatus = postStatus;
  }

  public static List<BasketResponse> listOf(List<Basket> baskets) {
    return baskets.stream().map(v ->
        BasketResponse.builder()
            .id(v.getId())
            .bookThumbnail(v.getPost().getBook().getBookThumbnail())
            .postTitle(v.getPost().getTitle())
            .bookTitle(v.getPost().getBook().getBookTitle())
            .bookPrice(v.getPost().getPrice())
            .postStatus(v.getPost().getPostStatus().name())
            .sellerIdentity(v.getPost().getSeller().getIdentity())
            .build()
    ).collect(Collectors.toList());
  }
}
