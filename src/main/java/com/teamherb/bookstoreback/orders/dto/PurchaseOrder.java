package com.teamherb.bookstoreback.orders.dto;

import com.teamherb.bookstoreback.orders.domain.Orders;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PurchaseOrder {

  private Long id;

  private String bookThumbnail;

  private String postTitle;

  private String bookTitle;

  private String bookPrice;

  private String sellerIdentity;

  private String orderStatus;


  @Builder
  public PurchaseOrder(Long id,String bookThumbnail, String postTitle, String bookTitle,
      String bookPrice, String sellerIdentity, String orderStatus) {
    this.id = id;
    this.bookThumbnail = bookThumbnail;
    this.postTitle = postTitle;
    this.bookTitle = bookTitle;
    this.bookPrice = bookPrice;
    this.sellerIdentity = sellerIdentity;
    this.orderStatus = orderStatus;
  }

  public static List<PurchaseOrder> listOf(List<Orders> purchaseOrders) {
    return purchaseOrders.stream().map(v ->
        PurchaseOrder.builder()
            .id(v.getId())
            .bookThumbnail(v.getPost().getBook().getBookThumbnail())
            .postTitle(v.getPost().getTitle())
            .bookTitle(v.getPost().getBook().getBookTitle())
            .bookPrice(v.getPost().getPrice())
            .sellerIdentity(v.getSeller().getIdentity())
            .orderStatus(v.getOrderStatus().name())
            .build()
    ).collect(Collectors.toList());
  }
}
