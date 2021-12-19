package com.bookwhale.auth.domain.provider;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RequiredArgsConstructor
@Component("GoogleOAuthProvider")
public class GoogleOAuthProvider implements OAuthProvider {
    @Value("${external-api.auth.google.client-id}")
    private String clientId;
    @Value("${external-api.auth.google.client-key}")
    private String clientSecret;
    @Value("${external-api.auth.google.base-url}")
    private String baseRequestURL;
    @Value("${external-api.auth.google.callback-url}")
    private String callbackURL;
    @Value("${external-api.auth.google.access-token-url}")
    private String accessTokenRequestURL;

    @Override
    public String getOAuthRedirectURL() {
        Map<String, Object> params = new HashMap<>();
        params.put("scope", "profile");
        params.put("response_type", "code");
        params.put("client_id", clientId);
        params.put("redirect_uri", callbackURL);

        String parameterString = params.entrySet().stream()
            .map(x -> x.getKey() + "=" + x.getValue())
            .collect(Collectors.joining("&"));

        return baseRequestURL + "?" + parameterString;
    }

    @Override
    public String requestAccessToken(String accessCode) {
        RestTemplate restTemplate = new RestTemplate();

        Map<String, Object> params = new HashMap<>();
        params.put("code", accessCode);
        params.put("client_id", clientId);
        params.put("client_secret", clientSecret);
        params.put("redirect_uri", callbackURL);
        params.put("grant_type", "authorization_code");

        ResponseEntity<String> responseEntity =
            restTemplate.postForEntity(accessTokenRequestURL, params, String.class);
        String body = responseEntity.getBody();
        log.info("조회된 body 확인 : {}", body);

        return body;
    }


}
