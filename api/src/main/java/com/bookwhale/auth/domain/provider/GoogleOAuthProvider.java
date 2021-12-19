package com.bookwhale.auth.domain.provider;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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
    public String requestAccessToken() {
        return null;
    }


}
