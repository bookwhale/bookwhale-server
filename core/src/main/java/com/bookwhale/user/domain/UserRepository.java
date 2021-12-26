package com.bookwhale.user.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsById(long id);

    boolean existsByEmail(String email);

    Optional<User> findById(long id);

    Optional<User> findByEmail(String email);

}
