package com.bookwhale.push.service;

import com.bookwhale.push.domain.FireBaseAccess;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class FirebaseCloudMessageService {

    private final FireBaseAccess fireBaseAccess;

    public ResponseEntity<String> sendMessageTo(String targetToken, String title, String body) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        String message = fireBaseAccess.makeMessage(targetToken, title, body);
        HttpHeaders requestHeader = new HttpHeaders();
        requestHeader.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        requestHeader.add(HttpHeaders.AUTHORIZATION, "Bearer " + fireBaseAccess.getAccessToken());

        HttpEntity<String> requestEntity = new HttpEntity<>(message, requestHeader);

        return restTemplate.exchange(fireBaseAccess.getApiUrl(), HttpMethod.POST, requestEntity,
            String.class);
    }
}
