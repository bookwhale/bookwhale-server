package com.teamherb.bookstoreback.account.domain;

import com.teamherb.bookstoreback.user.domain.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findAllByUser(User user);

    void deleteAllByUser(User user);

}
