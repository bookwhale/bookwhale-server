package com.bookwhale.message.domain;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MessageRepository extends MongoRepository<Message, Long> {

    Page<Message> findAllByRoomIdOrderByCreatedDateDesc(Long roomId, Pageable pageable);

    Optional<Message> findTopByRoomIdOrderByCreatedDateDesc(Long roomId);
}
