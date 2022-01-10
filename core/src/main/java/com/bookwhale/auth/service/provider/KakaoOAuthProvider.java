package com.bookwhale.auth.service.provider;

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
@Component("KakaoOAuthProvider")
public class KakaoOAuthProvider implements OAuthProvider {

    @Value("${external-api.auth.kakao.client-id}")
    private String clientId;
    @Value("${external-api.auth.kakao.client-key}")
    private String clientKey;
    @Value("${external-api.auth.kakao.base-url}")
    private String baseRequestURL;
    @Value("${external-api.auth.kakao.callback-url}")
    private String callbackURL;
    @Value("${external-api.auth.kakao.state}")
    private String state;
    @Value("${external-api.auth.kakao.access-token-url}")
    private String accessTokenRequestURL;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public String getOAuthRedirectURL() {
        Map<String, Object> params = new HashMap<>();
        params.put("client_id", clientId);
        params.put("redirect_uri", callbackURL);
        params.put("response_type", "code");
        params.put("state", Hashing.sha512().hashString(state, StandardCharsets.UTF_8).toString());

        String parameterString = params.entrySet().stream()
            .map(x -> x.getKey() + "=" + x.getValue()).collect(Collectors.joining("&"));

        return baseRequestURL + "/authorize" + "?" + parameterString;
    }

    @Override
    public ResponseEntity<String> requestAccessToken(String accessCode) {
        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", "authorization_code");
        requestBody.add("client_id", clientId);
        requestBody.add("client_secret", clientKey);
        requestBody.add("redirect_uri", callbackURL);
        requestBody.add("code", accessCode);
        requestBody.add("state",
            Hashing.sha512().hashString(state, StandardCharsets.UTF_8).toString());

        HttpHeaders requestHeader = new HttpHeaders();
        requestHeader.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        var requestEntity = new HttpEntity<>(requestBody, requestHeader);

        return restTemplate.exchange(accessTokenRequestURL, HttpMethod.POST, requestEntity,
            String.class);
    }

    public ResponseEntity<String> getUserInfoFromProvider(String accessToken) {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<>();
        String[] requestProperties = {"properties.nickname", "properties.profile_image", "kakao_account.name",
            "kakao_account.email"};

        requestBody.add("property_keys", requestProperties);
        HttpEntity<Map<String, Object>> request = new HttpEntity(requestBody, headers);


        String url = "https://kapi.kakao.com/v2/user/me";

        return restTemplate.exchange(url, HttpMethod.GET, request, String.class);
    }
}
