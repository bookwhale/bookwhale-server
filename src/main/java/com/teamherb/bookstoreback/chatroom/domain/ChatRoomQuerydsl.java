package com.teamherb.bookstoreback.chatroom.domain;

import com.teamherb.bookstoreback.user.domain.User;
import java.util.List;

public interface ChatRoomQuerydsl {

  public List<ChatRoom> findAllByBuyerOrSellerCreatedDateDesc(User loginUser);
}
