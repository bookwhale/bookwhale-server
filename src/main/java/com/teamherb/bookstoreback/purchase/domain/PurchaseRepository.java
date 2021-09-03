package com.teamherb.bookstoreback.purchase.domain;

import com.teamherb.bookstoreback.user.domain.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    List<Purchase> findAllByPurchaserOrderByCreatedDate(User user);
}