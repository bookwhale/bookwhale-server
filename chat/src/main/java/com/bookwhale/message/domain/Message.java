package com.bookwhale.message.domain;

import java.time.LocalDateTime;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document
public class Message {

  @Id
  private String id;

  private Long roomId;

  private Long senderId;

  private String senderIdentity;

  private String content;

  @CreatedDate
  private LocalDateTime createdDate;

  @Builder
  public Message(String id, Long roomId, Long senderId, String senderIdentity, String content,
      LocalDateTime createdDate) {
    this.id = id;
    this.roomId = roomId;
    this.senderId = senderId;
    this.senderIdentity = senderIdentity;
    this.content = content;
    this.createdDate = createdDate;
  }

  public static Message createEmptyMessage() {
    return Message.builder()
        .senderId(0L)
        .senderIdentity("")
        .content("")
        .createdDate(LocalDateTime.of(1, 1, 1, 1, 1, 1, 1))
        .build();
  }

  public static Message create(Message message) {
    return Message.builder()
        .roomId(message.getRoomId())
        .senderId(message.getSenderId())
        .senderIdentity(message.getSenderIdentity())
        .content(message.getContent())
        .build();
  }
}
