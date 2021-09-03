package com.teamherb.bookstoreback.orders.dto;

import com.teamherb.bookstoreback.orders.domain.Orders;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SaleOrder {

  private Long id;

  private String bookThumbnail;

  private String postTitle;

  private String bookTitle;

  private String bookPrice;

  private String purchaserIdentity;

  private String orderStatus;

  @Builder
  public SaleOrder(Long id,String bookThumbnail, String postTitle, String bookTitle,
      String bookPrice, String purchaserIdentity, String orderStatus) {
    this.id = id;
    this.bookThumbnail = bookThumbnail;
    this.postTitle = postTitle;
    this.bookTitle = bookTitle;
    this.bookPrice = bookPrice;
    this.purchaserIdentity = purchaserIdentity;
    this.orderStatus = orderStatus;
  }

  public static List<SaleOrder> listOf(List<Orders> saleOrders) {
    return saleOrders.stream().map(v ->
        SaleOrder.builder()
            .id(v.getId())
            .bookThumbnail(v.getPost().getBook().getBookThumbnail())
            .postTitle(v.getPost().getTitle())
            .bookTitle(v.getPost().getBook().getBookTitle())
            .bookPrice(v.getPost().getPrice())
            .purchaserIdentity(v.getPurchaser().getIdentity())
            .orderStatus(v.getOrderStatus().name())
            .build()
    ).collect(Collectors.toList());
  }
}
