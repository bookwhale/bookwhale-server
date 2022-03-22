package com.bookwhale.push.domain;

import com.bookwhale.common.exception.CustomException;
import com.bookwhale.common.exception.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

@Slf4j
public class FireBaseAccess {

    private final String firebaseConfigPath;
    private final String apiUrl;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public FireBaseAccess(String firebaseConfigPath, String apiUrl) {
        this.firebaseConfigPath = firebaseConfigPath;
        this.apiUrl = apiUrl;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public String getAccessToken() throws Exception {
        GoogleCredentials googleCredentials = GoogleCredentials.fromStream(
                new ClassPathResource(firebaseConfigPath).getInputStream())
            .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));

        // accessToken 생성
        googleCredentials.refreshIfExpired();

        // GoogleCredential의 getAccessToken으로 토큰 받아온 뒤, getTokenValue로 최종적으로 받음
        // REST API로 FCM에 push 요청 보낼 때 Header에 설정하여 인증을 위해 사용
        return googleCredentials.getAccessToken().getTokenValue();
    }

    public String makeMessage(String targetToken, String title, String body) {
        Notification notification = Notification.builder()
            .setTitle(title)
            .setBody(body)
            .build();
        Message message = Message.builder()
            .setToken(targetToken)
            .setNotification(notification)
            .build();

        String msg;
        try {
            msg = objectMapper.writeValueAsString(message);
            log.info(msg);
        } catch (JsonProcessingException e) {
            log.error("push 메시지를 json으로 변환하는 과정에서 오류 발생.");
            throw new CustomException(ErrorCode.FAILED_CONVERT_TO_JSON);
        }
        return msg;
    }

}
