package com.bookwhale.push.service;

import com.bookwhale.common.domain.ActiveYn;
import com.bookwhale.push.domain.FireBaseAccess;
import com.bookwhale.push.dto.PushMessageParams;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class PushService {

    private final FireBaseAccess fireBaseAccess;

    public void sendMessageTo(PushMessageParams pushMessageParams, Map<String, String> dataMap)
        throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        String message = fireBaseAccess.makeMessageJson(pushMessageParams.getTargetToken(),
            pushMessageParams.getTitle(), pushMessageParams.getBody(), dataMap);
        HttpHeaders requestHeader = new HttpHeaders();
        requestHeader.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        requestHeader.add(HttpHeaders.AUTHORIZATION, "Bearer " + fireBaseAccess.getAccessToken());

        HttpEntity<String> requestEntity = new HttpEntity<>(message, requestHeader);

        ResponseEntity<String> response = restTemplate.exchange(fireBaseAccess.getApiUrl(),
            HttpMethod.POST, requestEntity,
            String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            log.error("send push Message failed. / device : {}",
                pushMessageParams.getTargetToken());
        }
    }

    public void sendMessageFromFCM(PushMessageParams pushMessageParams, Map<String, String> dataMap)
        throws Exception {
        if (ActiveYn.Y.equals(pushMessageParams.getPushActivateStatus())) {
            Message message = fireBaseAccess.makeMessage(pushMessageParams.getTargetToken(),
                pushMessageParams.getTitle(), pushMessageParams.getBody(), dataMap);
            String response = FirebaseMessaging.getInstance(fireBaseAccess.getFirebaseApp())
                .send(message);

            if (StringUtils.isEmpty(response)) {
                log.error("send push Message failed. / response : {}", response);
            }
        }
    }

    public void sendMessageFromFCMWithoutNonitification(String deviceToken,
        Map<String, String> dataMap) throws Exception {
        Message message = fireBaseAccess.makeMessageWithoutNotification(deviceToken, dataMap);
        String response = FirebaseMessaging.getInstance(fireBaseAccess.getFirebaseApp())
            .send(message);

        if (StringUtils.isEmpty(response)) {
            log.error("send push Message failed. / response : {}", response);
        }
    }
}
