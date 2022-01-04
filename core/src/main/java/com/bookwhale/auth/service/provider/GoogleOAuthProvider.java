package com.bookwhale.auth.service.provider;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
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

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public String getOAuthRedirectURL() {
        Map<String, Object> params = new HashMap<>();
        params.put("scope",
            "https://www.googleapis.com/auth/userinfo.email https://www.googleapis.com/auth/userinfo.profile");
        params.put("response_type", "code");
        params.put("client_id", clientId);
        params.put("redirect_uri", callbackURL);

        String oAuthRequestUrl = "/v2/auth";
        String parameterString = params.entrySet().stream()
            .map(x -> x.getKey() + "=" + x.getValue())
            .collect(Collectors.joining("&"));

        return baseRequestURL + oAuthRequestUrl + "?" + parameterString;
    }

    @Override
    public ResponseEntity<String> requestAccessToken(String accessCode) {
        Map<String, Object> params = new HashMap<>();
        params.put("code", accessCode);
        params.put("client_id", clientId);
        params.put("client_secret", clientSecret);
        params.put("redirect_uri", callbackURL);
        params.put("grant_type", "authorization_code");

        return restTemplate.postForEntity(accessTokenRequestURL, params, String.class);
    }

    public ResponseEntity<String> getUserInfoFromProvider(String accessToken) {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        HttpEntity<Map<String, String>> request = new HttpEntity(headers);

        String url = "https://www.googleapis.com/oauth2/v2/userinfo";

        return restTemplate.exchange(url, HttpMethod.GET, request, String.class);
    }


}
