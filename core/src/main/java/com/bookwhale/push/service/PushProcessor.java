package com.bookwhale.push.service;

import com.bookwhale.article.domain.Article;
import com.bookwhale.chatroom.domain.ChatRoom;
import com.bookwhale.chatroom.domain.ChatRoomRepository;
import com.bookwhale.message.domain.Message;
import com.bookwhale.push.dto.PushMessageParams;
import com.bookwhale.push.dto.PushMessageParams.PushMessageParamsBuilder;
import com.bookwhale.user.domain.User;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class PushProcessor {

    private final PushService pushService;
    private final ChatRoomRepository chatRoomRepository;

    public void pushMessageOfCreatedChatRoom(
        User loginUser, User seller, Article article, ChatRoom chatRoom) {
        String message = String.format("채팅방이 생성되었습니다. / 판매글 : %s", article.getTitle());
        PushMessageParamsBuilder createChatRoomPushMessage = PushMessageParams.builder()
            .title("채팅방 생성 알림")
            .body(message);

        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("message ", message);
        dataMap.put("roomId", chatRoom.getId().toString());

        try {
            pushService.sendMessageFromFCM(
                createChatRoomPushMessage
                    .pushActivateStatus(loginUser.getPushActivate())
                    .targetToken(loginUser.getDeviceToken())
                    .build(), dataMap
            );
            pushService.sendMessageFromFCM(
                createChatRoomPushMessage
                    .pushActivateStatus(seller.getPushActivate())
                    .targetToken(seller.getDeviceToken())
                    .build(), dataMap
            );
        } catch (Exception e) {
            log.error("채팅방 생성 알림 동작에 오류가 발생하였습니다.", e);
        }
    }

    public void pushMessageOfChatMessage(Message chatMessage) {
        String message = chatMessage.getContent();
        Long roomId = chatMessage.getRoomId();
        Long senderId = chatMessage.getSenderId();

        try {
            ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("room 확인 실패"));

            PushMessageParamsBuilder createChatRoomPushMessage = PushMessageParams.builder()
                .title(chatRoom.getArticle().getTitle())
                .body(message);

            User buyer = chatRoom.getBuyer();
            User seller = chatRoom.getSeller();

            Map<String, String> dataMap = new HashMap<>();
            dataMap.put("description", message);
            dataMap.put("roomId", roomId.toString());
            dataMap.put("senderId", senderId.toString());
            dataMap.put("articleTitle", chatRoom.getArticle().getTitle());

            if (senderId.equals(buyer.getId())) {
                pushService.sendMessageFromFCM(
                    createChatRoomPushMessage
                        .pushActivateStatus(seller.getPushActivate())
                        .targetToken(seller.getDeviceToken()).build(),
                    dataMap
                );
            } else {
                pushService.sendMessageFromFCM(
                    createChatRoomPushMessage
                        .pushActivateStatus(buyer.getPushActivate())
                        .targetToken(buyer.getDeviceToken()).build(),
                    dataMap
                );
            }
        } catch (Exception e) {
            log.error("채팅 메시지 알림 동작에 오류가 발생하였습니다.", e);
        }
    }
}
