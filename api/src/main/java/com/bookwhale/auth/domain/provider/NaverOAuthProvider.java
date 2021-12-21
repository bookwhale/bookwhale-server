package com.bookwhale.auth.domain.provider;

import com.google.common.hash.Hashing;
import java.nio.charset.StandardCharsets;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RequiredArgsConstructor
@Component("NaverOAuthProvider")
public class NaverOAuthProvider implements OAuthProvider {

    @Value("${external-api.auth.naver.client-id}")
    private String clientId;
    @Value("${external-api.auth.naver.client-key}")
    private String clientSecret;
    @Value("${external-api.auth.naver.base-url}")
    private String baseRequestURL;
    @Value("${external-api.auth.naver.callback-url}")
    private String callbackURL;
    @Value("${external-api.auth.naver.state}")
    private String state;
    @Value("${external-api.auth.naver.access-token-url}")
    private String accessTokenRequestURL;

    @Override
    public String getOAuthRedirectURL() {
        Map<String, Object> params = new HashMap<>();
        params.put("response_type", "code");
        params.put("client_id", clientId);
        params.put("redirect_uri", callbackURL);
        params.put("state", Hashing.sha512().hashString(state, StandardCharsets.UTF_8).toString());

        String parameterString = params.entrySet().stream()
            .map(x -> x.getKey() + "=" + x.getValue()).collect(Collectors.joining("&"));

        return baseRequestURL + "?" + parameterString;
    }

    @Override
    public ResponseEntity<String> requestAccessToken(String accessCode) {
        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", "authorization_code");
        requestBody.add("client_id", clientId);
        requestBody.add("client_secret", clientSecret);
        requestBody.add("code", accessCode);
        requestBody.add("state",
            Hashing.sha512().hashString(state, StandardCharsets.UTF_8).toString());

        HttpHeaders requestHeader = new HttpHeaders();
        requestHeader.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        var requestEntity = new HttpEntity<>(requestBody, requestHeader);

        return restTemplate.exchange(accessTokenRequestURL, HttpMethod.POST, requestEntity,
            String.class);
    }


}
