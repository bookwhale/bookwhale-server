package com.bookwhale.push.domain;

import com.bookwhale.common.exception.CustomException;
import com.bookwhale.common.exception.ErrorCode;
import com.bookwhale.push.dto.FirebaseCloudMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FireBaseAccess {

    private final GoogleCredentials googleCredentials;
    private final String apiUrl;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final FirebaseApp firebaseApp;

    public FireBaseAccess(GoogleCredentials googleCredentials, String apiUrl) {
        this.googleCredentials = googleCredentials;
        this.apiUrl = apiUrl;

        if (googleCredentials != null) {
            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(this.googleCredentials)
                .build();

            this.firebaseApp = FirebaseApp.initializeApp(options);
        } else {
            this.firebaseApp = null;
        }
    }

    public FirebaseApp getFirebaseApp() {
        return firebaseApp;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public String getAccessToken(){
        // accessToken 생성
        try {
            googleCredentials.refreshIfExpired();
        } catch (IOException e) {
            log.error("googleCredential refresh failed.", e);
        }

        // GoogleCredential의 getAccessToken으로 토큰 받아온 뒤, getTokenValue로 최종적으로 받음
        // REST API로 FCM에 push 요청 보낼 때 Header에 설정하여 인증을 위해 사용
        return googleCredentials.getAccessToken().getTokenValue();
    }

    public Message makeMessage(String targetToken, String title, String body) {
        Notification notification = Notification.builder()
            .setTitle(title)
            .setBody(body)
            .build();

        return Message.builder()
            .setToken(targetToken)
            .setNotification(notification)
            .build();
    }

    public String makeMessageJson(String targetToken, String title, String body) {
        FirebaseCloudMessage fcm = FirebaseCloudMessage.builder()
            .message(
                FirebaseCloudMessage.Message.builder()
                    .token(targetToken)
                    .notification(
                        FirebaseCloudMessage.Notification.builder()
                            .title(title)
                            .body(body)
                            .image(null)
                            .build()
                    )
                    .build()
            ).build();

        String msg;
        try {
            msg = objectMapper.writeValueAsString(fcm);
        } catch (JsonProcessingException e) {
            log.error("push 메시지를 json으로 변환하는 과정에서 오류 발생.");
            throw new CustomException(ErrorCode.FAILED_CONVERT_TO_JSON);
        }
        return msg;
    }

}
