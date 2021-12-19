package com.bookwhale.auth.domain.provider;

import com.google.common.hash.Hashing;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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

    @Override
    public String getOAuthRedirectURL() {
        Map<String, Object> params = new HashMap<>();
        params.put("response_type", "code");
        params.put("client_id", clientId);
        params.put("redirect_uri", callbackURL);
        params.put("state", Hashing.sha512().hashString(state, StandardCharsets.UTF_8).toString());

        String parameterString = params.entrySet().stream()
            .map(x -> x.getKey() + "=" + x.getValue())
            .collect(Collectors.joining("&"));

        return baseRequestURL + "?" + parameterString;
    }

    @Override
    public String requestAccessToken() {
        return null;
    }


}
