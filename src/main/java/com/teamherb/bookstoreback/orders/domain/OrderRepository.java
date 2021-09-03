package com.teamherb.bookstoreback.orders.domain;

import com.teamherb.bookstoreback.user.domain.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Orders, Long> {

    List<Orders> findAllBySellerOrderByCreatedDate(User user);

    List<Orders> findAllByPurchaserOrderByCreatedDate(User user);

    Optional<Orders> findById(Long id);
}
