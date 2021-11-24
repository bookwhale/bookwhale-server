package com.bookwhale.chatroom.domain;

import com.bookwhale.user.domain.User;
import java.util.List;

public interface ChatRoomQuerydsl {

    List<ChatRoom> findAllByBuyerOrSellerCreatedDateDesc(User loginUser);
}
