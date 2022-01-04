package com.bookwhale.chatroom.dto;

import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChatRoomCreateRequest {

    @NotNull
    private Long articleId;

    @NotNull
    private Long sellerId;

    @Builder
    public ChatRoomCreateRequest(Long articleId, Long sellerId) {
        this.articleId = articleId;
        this.sellerId = sellerId;
    }
}
