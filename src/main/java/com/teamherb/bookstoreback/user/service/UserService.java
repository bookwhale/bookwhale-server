package com.teamherb.bookstoreback.user.service;

import com.teamherb.bookstoreback.orders.domain.OrderRepository;
import com.teamherb.bookstoreback.orders.domain.Orders;
import com.teamherb.bookstoreback.orders.dto.PurchaseOrder;
import com.teamherb.bookstoreback.orders.dto.SaleOrder;
import com.teamherb.bookstoreback.purchase.domain.Purchase;
import com.teamherb.bookstoreback.purchase.domain.PurchaseRepository;
import com.teamherb.bookstoreback.purchase.dto.PurchaseResponse;
import com.teamherb.bookstoreback.sale.domain.Sale;
import com.teamherb.bookstoreback.sale.domain.SaleRepository;
import com.teamherb.bookstoreback.sale.dto.SaleResponse;
import com.teamherb.bookstoreback.common.exception.CustomException;
import com.teamherb.bookstoreback.common.exception.dto.ErrorCode;
import com.teamherb.bookstoreback.user.domain.Role;
import com.teamherb.bookstoreback.user.domain.User;
import com.teamherb.bookstoreback.user.domain.UserRepository;
import com.teamherb.bookstoreback.user.dto.SignUpRequest;
import com.teamherb.bookstoreback.user.dto.UserUpdateRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

  private final UserRepository userRepository;

  private final PurchaseRepository purchaseRepository;

  private final SaleRepository saleRepository;

  private final PasswordEncoder passwordEncoder;

  private final OrderRepository orderRepository;

  public void createUser(SignUpRequest signUpRequest) {
    if (userRepository.existsByIdentity(signUpRequest.getIdentity())) {
      throw new CustomException(ErrorCode.DUPLICATED_USER_IDENTITY);
    }

    User user = User.builder()
        .identity(signUpRequest.getIdentity())
        .password(passwordEncoder.encode(signUpRequest.getPassword()))
        .email(signUpRequest.getEmail())
        .name(signUpRequest.getName())
        .role(Role.ROLE_USER)
        .build();

    userRepository.save(user);
  }

  public void updateMyInfo(User user, UserUpdateRequest userUpdateRequest) {
    user.update(userUpdateRequest);
    userRepository.save(user);
  }

  @Transactional(readOnly = true)
  public List<PurchaseResponse> findPurchaseHistories(User user) {
    List<Purchase> purchases = purchaseRepository.findAllByPurchaserOrderByCreatedDate(user);
    return PurchaseResponse.listOf(purchases);
  }

  @Transactional(readOnly = true)
  public List<SaleResponse> findSaleHistories(User user) {
    List<Sale> sales = saleRepository.findAllBySellerOrderByCreatedDate(user);
    return SaleResponse.listOf(sales);
  }

  @Transactional(readOnly = true)
  public List<SaleOrder> findSaleOrders(User user) {
    List<Orders> saleOrders = orderRepository
        .findAllBySellerOrderByCreatedDate(user);
    return SaleOrder.listOf(saleOrders);
  }


  @Transactional(readOnly = true)
  public List<PurchaseOrder> findPurchaseOrders(User user) {
    List<Orders> purchaseOrders = orderRepository
        .findAllByPurchaserOrderByCreatedDate(user);
    return PurchaseOrder.listOf(purchaseOrders);
  }
}
