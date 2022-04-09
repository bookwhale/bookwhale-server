package com.bookwhale.chatroom.dto;

import com.bookwhale.chatroom.domain.ChatRoom;
import com.bookwhale.message.domain.Message;
import com.bookwhale.user.domain.User;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChatRoomResponse {

    private Long roomId;
    private Long articleId;
    private String articleTitle;
    private String articleImage;
    private String opponentIdentity;
    private String opponentProfile;
    private boolean isOpponentDelete;
    private LocalDateTime roomCreateAt;
    private String lastContent;
    private LocalDateTime lastContentCreateAt;

    @Builder
    public ChatRoomResponse(Long roomId, Long articleId, String articleTitle,
        String articleImage, String opponentIdentity, String opponentProfile,
        boolean isOpponentDelete,
        LocalDateTime roomCreateAt, String lastContent, LocalDateTime lastContentCreateAt) {
        this.roomId = roomId;
        this.articleId = articleId;
        this.articleTitle = articleTitle;
        this.articleImage = articleImage;
        this.opponentIdentity = opponentIdentity;
        this.opponentProfile = opponentProfile;
        this.isOpponentDelete = isOpponentDelete;
        this.roomCreateAt = roomCreateAt;
        this.lastContent = lastContent;
        this.lastContentCreateAt = lastContentCreateAt;
    }

    public static ChatRoomResponse of(ChatRoom chatRoom, Message lastMessage, User loginUser) {
        return ChatRoomResponse.builder()
            .roomId(chatRoom.getId())
            .articleId(chatRoom.getArticle().getId())
            .articleTitle(chatRoom.getArticle().getTitle())
            .articleImage(chatRoom.getArticle().getImages().getFirstImageUrl())
            .opponentIdentity(chatRoom.getOpponent(loginUser).getIdentity())
            .opponentProfile(chatRoom.getOpponent(loginUser).getProfile())
            .roomCreateAt(chatRoom.getCreatedDate())
            .isOpponentDelete(chatRoom.isOpponentDelete(loginUser))
            .lastContent(lastMessage.getContent())
            .lastContentCreateAt(lastMessage.getCreatedDate())
            .build();
    }
}
