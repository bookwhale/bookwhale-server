package com.teamherb.bookstoreback.chatroom.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long>, ChatRoomQuerydsl {

}
