package com.bookwhale.chatroom.dto;

import com.bookwhale.chatroom.domain.ChatRoom;
import com.bookwhale.chatroom.domain.Opponent;
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
    private String lastContent;

    @Builder
    public ChatRoomResponse(Long roomId, Long articleId, String articleTitle,
        String articleImage, String opponentIdentity, String opponentProfile,
        boolean isOpponentDelete,
        String lastContent) {
        this.roomId = roomId;
        this.articleId = articleId;
        this.articleTitle = articleTitle;
        this.articleImage = articleImage;
        this.opponentIdentity = opponentIdentity;
        this.opponentProfile = opponentProfile;
        this.isOpponentDelete = isOpponentDelete;
        this.lastContent = lastContent;
    }

    public static ChatRoomResponse of(ChatRoom chatRoom, Opponent opponent,
        boolean isOpponentDelete, String lastContent) {
        return ChatRoomResponse.builder()
            .roomId(chatRoom.getId())
            .articleId(chatRoom.getArticle().getId())
            .articleTitle(chatRoom.getArticle().getTitle())
            .articleImage(chatRoom.getArticle().getImages().getFirstImageUrl())
            .opponentIdentity(opponent.getIdentity())
            .opponentProfile(opponent.getProfile())
            .isOpponentDelete(isOpponentDelete)
            .lastContent(lastContent)
            .build();
    }
}
