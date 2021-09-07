package com.teamherb.bookstoreback.basket.domain;

import com.teamherb.bookstoreback.user.domain.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BasketRepository extends JpaRepository<Basket, Long> {

  List<Basket> findAllByPurchaserOrderByCreatedDate(User user);

}
