package com.teamherb.bookstoreback.message.domain;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {

  Page<Message> findAllByRoomIdOrderByCreatedDateDesc(Long roomId, Pageable pageable);

  Optional<Message> findTopByRoomIdOrderByCreatedDateDesc(Long roomId);
}
