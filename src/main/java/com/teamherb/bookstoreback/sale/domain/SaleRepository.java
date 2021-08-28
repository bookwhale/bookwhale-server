package com.teamherb.bookstoreback.sale.domain;

import com.teamherb.bookstoreback.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SaleRepository extends JpaRepository<Sale,Long> {

    List<Sale> findAllBySellerOrderByCreatedDate(User user);
}
