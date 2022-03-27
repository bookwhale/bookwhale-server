package com.bookwhale.chatroom.service;

import com.bookwhale.article.domain.Article;
import com.bookwhale.article.domain.ArticleRepository;
import com.bookwhale.chatroom.domain.ChatRoom;
import com.bookwhale.chatroom.domain.ChatRoomRepository;
import com.bookwhale.chatroom.dto.ChatRoomCreateRequest;
import com.bookwhale.chatroom.dto.ChatRoomResponse;
import com.bookwhale.common.exception.CustomException;
import com.bookwhale.common.exception.ErrorCode;
import com.bookwhale.message.domain.Message;
import com.bookwhale.message.domain.MessageRepository;
import com.bookwhale.push.service.PushService;
import com.bookwhale.user.domain.User;
import com.bookwhale.user.service.UserService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ArticleRepository articleRepository;
    private final MessageRepository messageRepository;
    private final UserService userService;
    private final PushService pushService;

    public void createChatRoom(User user, ChatRoomCreateRequest request) {
        User loginUser = userService.findUserByEmail(user.getEmail());
        User seller = getSellerUser(request.getSellerId());
        Article article = getArticleByArticleId(request.getArticleId());
        article.validateArticleStatus();
        chatRoomRepository.save(ChatRoom.create(article, loginUser, seller));

        try {
            pushService.sendMessageTo(loginUser.getDeviceToken(),
                "채팅방 생성 알림",
                String.format("채팅방이 생성되었습니다. / 판매글 : %s", article.getTitle())
            );
            pushService.sendMessageTo(seller.getDeviceToken(),
                "채팅방 생성 알림",
                String.format("채팅방이 생성되었습니다. / 판매글 : %s", article.getTitle())
            );
        } catch (Exception e) {
            log.error("채팅방 생성 알림 동작에 오류가 발생하였습니다.", e);
        }

    }

    public User getSellerUser(Long sellerId) {
        return userService.findByUserId(sellerId)
            .orElseThrow(() -> new CustomException(ErrorCode.INVALID_SELLER_ID));
    }

    public Article getArticleByArticleId(Long articleId) {
        return articleRepository.findById(articleId)
            .orElseThrow(() -> new CustomException(ErrorCode.INVALID_ARTICLE_ID));
    }

    @Transactional(readOnly = true)
    public List<ChatRoomResponse> findChatRooms(User user) {
        User loginUser = userService.findUserByEmail(user.getEmail());
        List<ChatRoom> rooms = chatRoomRepository.findAllByBuyerOrSellerCreatedDateDesc(loginUser);
        return checkRoomsAndGetRoomResponses(rooms, loginUser);
    }

    private List<ChatRoomResponse> checkRoomsAndGetRoomResponses(List<ChatRoom> rooms,
        User loginUser) {
        //내가 떠난 채팅방은 제외
        //상대방이 나간 채팅방인지 확인
        List<ChatRoomResponse> list = new ArrayList<>();
        for (ChatRoom room : rooms) {
            if (!room.isLoginUserDelete(loginUser)) {
                Message lastMessage = messageRepository.findTopByRoomIdOrderByCreatedDateDesc(room.getId())
                    .orElseGet(Message::createEmptyMessage);
                ChatRoomResponse of = ChatRoomResponse.of(room, room.getOpponent(loginUser),
                    room.isOpponentDelete(loginUser), lastMessage.getContent());
                list.add(of);
            }
        }
        return list;
    }

    public void deleteChatRoom(User user, Long roomId) {
        User loginUser = userService.findUserByEmail(user.getEmail());
        ChatRoom room = validateRoomIdAndGetRoom(roomId);
        room.deleteChatRoom(loginUser);
        if (room.isEmpty()) {
            chatRoomRepository.deleteById(roomId);
        }
    }

    public ChatRoom validateRoomIdAndGetRoom(Long roomId) {
        return chatRoomRepository.findById(roomId)
            .orElseThrow(() -> new CustomException(ErrorCode.INVALID_CHATROOM_ID));
    }
}