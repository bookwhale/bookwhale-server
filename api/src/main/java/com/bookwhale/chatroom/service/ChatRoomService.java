package com.bookwhale.chatroom.service;

import com.bookwhale.article.domain.Article;
import com.bookwhale.article.domain.ArticleRepository;
import com.bookwhale.chatroom.domain.ChatRoom;
import com.bookwhale.chatroom.domain.ChatRoomRepository;
import com.bookwhale.chatroom.dto.ChatRoomCreateRequest;
import com.bookwhale.chatroom.dto.ChatRoomResponse;
import com.bookwhale.common.exception.CustomException;
import com.bookwhale.common.exception.ErrorCode;
import com.bookwhale.user.domain.User;
import com.bookwhale.user.domain.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    private final UserRepository userRepository;

    private final ArticleRepository articleRepository;

    public void createChatRoom(User user, ChatRoomCreateRequest request) {
        User seller = validateSellerIdAndGetSeller(request.getSellerId());
        Article article = getArticleByArticleId(request.getArticleId());
        article.validateArticleStatus();
        chatRoomRepository.save(ChatRoom.create(article, user, seller));
    }

    public User validateSellerIdAndGetSeller(Long sellerId) {
        return userRepository.findById(sellerId)
            .orElseThrow(() -> new CustomException(ErrorCode.INVALID_SELLER_ID));
    }

    public Article getArticleByArticleId(Long articleId) {
        return articleRepository.findById(articleId)
            .orElseThrow(() -> new CustomException(ErrorCode.INVALID_ARTICLE_ID));
    }

    @Transactional(readOnly = true)
    public List<ChatRoomResponse> findChatRooms(User user) {
        List<ChatRoom> rooms = chatRoomRepository.findAllByBuyerOrSellerCreatedDateDesc(user);
        return checkRoomsAndGetRoomResponses(rooms, user);
    }

    private List<ChatRoomResponse> checkRoomsAndGetRoomResponses(List<ChatRoom> rooms,
        User loginUser) {
        return rooms.stream()
            //내가 떠난 채팅방은 제외
            .filter(room -> !room.isLoginUserDelete(loginUser))
            //상대방이 나간 채팅방인지 확인
            .map(room -> ChatRoomResponse.of(room, room.getOpponent(loginUser),
                room.isOpponentDelete(loginUser)))
            .collect(Collectors.toList());
    }

    public void deleteChatRoom(User user, Long roomId) {
        ChatRoom room = validateRoomIdAndGetRoom(roomId);
        room.deleteChatRoom(user);
        if (room.isEmpty()) {
            chatRoomRepository.deleteById(roomId);
        }
    }

    public ChatRoom validateRoomIdAndGetRoom(Long roomId) {
        return chatRoomRepository.findById(roomId)
            .orElseThrow(() -> new CustomException(ErrorCode.INVALID_CHATROOM_ID));
    }
}
