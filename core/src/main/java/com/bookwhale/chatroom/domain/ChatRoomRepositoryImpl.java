package com.bookwhale.chatroom.domain;

import static com.bookwhale.chatroom.domain.QChatRoom.chatRoom;
import static com.bookwhale.article.domain.QArticle.article;
import static com.bookwhale.user.domain.QUser.user;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.bookwhale.user.domain.User;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ChatRoomRepositoryImpl implements ChatRoomQuerydsl {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ChatRoom> findAllByBuyerOrSellerCreatedDateDesc(User loginUser) {
        return queryFactory.selectFrom(chatRoom)
            .leftJoin(chatRoom.article, article).fetchJoin()
            .leftJoin(chatRoom.buyer, user).fetchJoin()
            .leftJoin(chatRoom.seller, user).fetchJoin()
            .where(chatRoom.buyer.eq(loginUser).or(chatRoom.seller.eq(loginUser)))
            .orderBy(chatRoom.createdDate.desc())
            .fetch();
    }
}
