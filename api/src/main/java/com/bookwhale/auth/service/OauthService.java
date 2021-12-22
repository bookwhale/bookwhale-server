package com.bookwhale.auth.service;

import com.bookwhale.auth.domain.provider.GoogleOAuthProvider;
import com.bookwhale.auth.domain.provider.NaverOAuthProvider;
import com.bookwhale.auth.domain.provider.OAuthProvider;
import com.bookwhale.auth.domain.provider.OAuthProviderType;
import com.bookwhale.auth.domain.token.GoogleOAuthToken;
import com.bookwhale.auth.domain.token.OAuthToken;
import com.bookwhale.auth.dto.OAuthLoginResponse;
import com.bookwhale.common.exception.CustomException;
import com.bookwhale.common.exception.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class OauthService {

    private final Map<String, OAuthProvider> oAuthProviders;
    private final HttpServletResponse response;

    public void sendLoginRequest(OAuthProviderType providerType) {
        try {
            String redirectUrl = null;
            if (providerType.equals(OAuthProviderType.GOOGLE)) {
                GoogleOAuthProvider oAuthProvider = (GoogleOAuthProvider) oAuthProviders.get(
                    "GoogleOAuthProvider");
                redirectUrl = oAuthProvider.getOAuthRedirectURL();
            } else if (providerType.equals(OAuthProviderType.NAVER)) {
                NaverOAuthProvider oAuthProvider = (NaverOAuthProvider) oAuthProviders.get(
                    "NaverOAuthProvider");
                redirectUrl = oAuthProvider.getOAuthRedirectURL();
            }

            response.sendRedirect(redirectUrl);
        } catch (IOException e) {
            log.debug("로그인 페이지 redirection 실패", e);
        }
    }

    public OAuthLoginResponse loginProcess(OAuthProviderType providerType, String accessCode) {
        String result = null;
        if (providerType.equals(OAuthProviderType.GOOGLE)) {
            GoogleOAuthProvider oAuthProvider = (GoogleOAuthProvider) oAuthProviders.get(
                "GoogleOAuthProvider");
            ResponseEntity<String> accessTokenResponse = oAuthProvider.requestAccessToken(
                accessCode);

            if (!accessTokenResponse.getStatusCode().equals(HttpStatus.OK)) {
                throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
            }

            GoogleOAuthToken accessToken = (GoogleOAuthToken) getTokenFromResponse(
                accessTokenResponse, providerType);
            ResponseEntity<String> userInfoResponse = oAuthProvider.getUserInfoFromProvider(
                accessToken);

            if (!userInfoResponse.getStatusCode().equals(HttpStatus.OK)) {
                throw new CustomException(ErrorCode.INFORMATION_NOT_FOUND);
            }

            log.info("조회결과 확인 : {}", userInfoResponse);

        } else if (providerType.equals(OAuthProviderType.NAVER)) {
            NaverOAuthProvider oAuthProvider = (NaverOAuthProvider) oAuthProviders.get(
                "NaverOAuthProvider");
            oAuthProvider.requestAccessToken(accessCode);
        }
        return new OAuthLoginResponse(result);
    }

    private OAuthToken getTokenFromResponse(ResponseEntity<String> response,
        OAuthProviderType providerType) {
        OAuthToken oAuthToken = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            if (providerType.equals(OAuthProviderType.GOOGLE)) {
                String body = response.getBody().replaceAll("\\R", "");
                oAuthToken = objectMapper.readValue(body, GoogleOAuthToken.class);
            } else if (providerType.equals(OAuthProviderType.NAVER)) {
                // TODO naver token 변환 추가
            }
        } catch (JsonProcessingException e) {
            log.debug("token converting failed.", e);
            throw new CustomException(ErrorCode.INVALID_ACCESS_TOKEN);
        }

        return oAuthToken;
    }

}
