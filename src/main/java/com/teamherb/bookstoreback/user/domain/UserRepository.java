package com.teamherb.bookstoreback.user.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByIdentity(String identity);

    Optional<User> findByIdentity(String identity);
}
