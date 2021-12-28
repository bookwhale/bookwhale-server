package com.bookwhale.message.dto;

import com.bookwhale.message.domain.Message;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MessageResponse {

    private Long senderId;
    private String senderIdentity;
    private String content;
    private LocalDateTime createdDate;

    @Builder
    public MessageResponse(Long senderId, String senderIdentity, String content,
        LocalDateTime createdDate) {
        this.senderId = senderId;
        this.senderIdentity = senderIdentity;
        this.content = content;
        this.createdDate = createdDate;
    }

    public static MessageResponse of(Message message) {
        return MessageResponse.builder()
            .senderId(message.getSenderId())
            .senderIdentity(message.getSenderIdentity())
            .content(message.getContent())
            .createdDate(message.getCreatedDate())
            .build();
    }

    public static List<MessageResponse> listOf(List<Message> messages) {
        return messages.stream().map(MessageResponse::of).collect(Collectors.toList());
    }
}
