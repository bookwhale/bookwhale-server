package com.bookwhale.message.dto;

import com.bookwhale.message.domain.Message;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MessageRequest {

    private Long roomId;
    private Long senderId;
    private String senderIdentity;
    private String content;

    public MessageRequest(Long roomId, Long senderId, String senderIdentity, String content) {
        this.roomId = roomId;
        this.senderId = senderId;
        this.senderIdentity = senderIdentity;
        this.content = content;
    }

    public Message toEntity() {
        return Message.builder()
            .roomId(roomId)
            .senderId(senderId)
            .senderIdentity(senderIdentity)
            .content(content)
            .build();
    }
}
