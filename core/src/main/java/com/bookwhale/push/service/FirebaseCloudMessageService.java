package com.bookwhale.push.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import java.util.List;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

public class FirebaseCloudMessageService {
    private final String API_URL = "fcm project url";

    public void sendMessageTo(String targetToken, String title, String body, String path) throws Exception {
        String message = makeMessage(targetToken, title, body, path);
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders requestHeader = new HttpHeaders();
        requestHeader.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        requestHeader.add(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken());

        var requestEntity = new HttpEntity<>(requestBody, requestHeader);

        return restTemplate.exchange(accessTokenRequestURL, HttpMethod.POST, requestEntity,
            String.class);
    }

    private String makeMessage(String targetToken, String title, String body, String path) throws JsonProcessingException {
        LinkedMultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        Notification notification = Notification.builder()
            .setTitle(title)
            .setBody(body)
            .build();
        Message message = Message.builder()
            .setToken(targetToken)
            .setNotification(notification)
            .build();

        log.info(objectMapper.writeValueAsString(fcmMessage));
        return objectMapper.writeValueAsString(fcmMessage);
    }

    private String getAccessToken() throws Exception {
        // 2)
        String firebaseConfigPath = "key_path;

        GoogleCredentials googleCredentials = GoogleCredentials.fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
            .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));

        // accessToken 생성
        googleCredentials.refreshIfExpired();

        // GoogleCredential의 getAccessToken으로 토큰 받아온 뒤, getTokenValue로 최종적으로 받음
        // REST API로 FCM에 push 요청 보낼 때 Header에 설정하여 인증을 위해 사용
        return googleCredentials.getAccessToken().getTokenValue();
    }
}
