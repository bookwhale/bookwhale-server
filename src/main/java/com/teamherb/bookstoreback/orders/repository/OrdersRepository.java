package com.teamherb.bookstoreback.orders.repository;

import com.teamherb.bookstoreback.orders.domain.Orders;
import com.teamherb.bookstoreback.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


public interface OrdersRepository extends JpaRepository<Orders,Long> {

    List<Orders> findAllByPurchaserOrderByCompletedate(User user);

    List<Orders> findAllBySellerOrderByCompletedate(User user);
}
