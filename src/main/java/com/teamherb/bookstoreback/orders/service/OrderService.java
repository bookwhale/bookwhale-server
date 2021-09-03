package com.teamherb.bookstoreback.orders.service;

import com.teamherb.bookstoreback.orders.domain.OrderRepository;
import com.teamherb.bookstoreback.orders.domain.Orders;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

  private final OrderRepository orderRepository;

  public void acceptPurchase(Long id) {
    Optional<Orders> findOrder = orderRepository.findById(id);
    findOrder.ifPresent(orders -> orders.acceptPurchase(orders));
  }

  public void cancelSale(Long id) {
    Optional<Orders> findOrder = orderRepository.findById(id);
    findOrder.ifPresent(orders -> orders.cancelSale(orders));
  }

  public void cancelPurchase(Long id) {
    Optional<Orders> findOrder = orderRepository.findById(id);
    findOrder.ifPresent(orders -> orders.cancelPurchase(orders));
  }
}
