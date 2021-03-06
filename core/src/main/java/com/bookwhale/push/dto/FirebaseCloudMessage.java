package com.bookwhale.push.dto;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class FirebaseCloudMessage {
    private Message message;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("FirebaseCloudMessage{");
        sb.append("message=").append(message);
        sb.append('}');
        return sb.toString();
    }

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Message {
        private String token; // 특정 device에 알림을 보내기위해 사용
        private Notification notification; // 모든 mobile os를 아우를수 있는 Notification
        private Map<String, String> data; // 포그라운드 알림을 위한 데이터

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("Message{");
            sb.append("token='").append(token).append('\'');
            sb.append(", notification=").append(notification);
            sb.append(", data=").append(data);
            sb.append('}');
            return sb.toString();
        }
    }

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Notification {
        private String title;
        private String body;
        private String image;

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("Notification{");
            sb.append("title='").append(title).append('\'');
            sb.append(", body='").append(body).append('\'');
            sb.append(", image='").append(image).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }
}
