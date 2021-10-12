package com.teamherb.bookstoreback.chatroom.domain;

import static com.teamherb.bookstoreback.chatroom.domain.QChatRoom.chatRoom;
import static com.teamherb.bookstoreback.post.domain.QPost.post;
import static com.teamherb.bookstoreback.user.domain.QUser.user;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.teamherb.bookstoreback.user.domain.User;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ChatRoomRepositoryImpl implements ChatRoomQuerydsl {

  private final JPAQueryFactory queryFactory;

  @Override
  public List<ChatRoom> findAllByBuyerOrSellerCreatedDateDesc(User loginUser) {
    return queryFactory.selectFrom(chatRoom)
        .leftJoin(chatRoom.post, post).fetchJoin()
        .leftJoin(chatRoom.buyer, user).fetchJoin()
        .leftJoin(chatRoom.seller, user).fetchJoin()
        .where(chatRoom.buyer.eq(loginUser).or(chatRoom.seller.eq(loginUser)))
        .orderBy(chatRoom.createdDate.desc())
        .fetch();
  }
}
