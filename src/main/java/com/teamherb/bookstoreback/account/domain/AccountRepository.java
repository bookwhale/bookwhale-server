package com.teamherb.bookstoreback.account.domain;

import com.teamherb.bookstoreback.user.domain.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Long countByUser(User user);

    List<Account> findAllByUser(User user);

    Optional<Account> findAccountByIdAndUser(Long id, User user);
}
