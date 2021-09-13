package com.teamherb.bookstoreback.Interest.domain;

import com.teamherb.bookstoreback.user.domain.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterestRepository extends JpaRepository<Interest, Long> {

  List<Interest> findAllByUserOrderByCreatedDate(User user);
}
