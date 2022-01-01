package com.bookwhale.auth.dto;

import com.bookwhale.auth.domain.JWT;
import com.bookwhale.auth.domain.JWT.Claims;
import com.bookwhale.auth.domain.JWT.ClaimsForRefresh;
import com.bookwhale.auth.domain.info.UserInfo;
import com.bookwhale.auth.domain.info.UserInfoFromGoogle;
import com.bookwhale.auth.domain.info.UserInfoFromNaver;
import com.bookwhale.auth.domain.token.GoogleOAuthToken;
import com.bookwhale.auth.domain.token.NaverOAuthToken;
import com.bookwhale.auth.domain.token.OAuthToken;
import com.bookwhale.auth.service.provider.GoogleOAuthProvider;
import com.bookwhale.auth.service.provider.NaverOAuthProvider;
import com.bookwhale.auth.service.provider.OAuthProvider;
import com.bookwhale.auth.service.provider.OAuthProviderType;
import com.bookwhale.common.exception.CustomException;
import com.bookwhale.common.exception.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Slf4j
public class OAuthObjectConverter {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Optional<UserInfo> getUserInfoResponseFromProvider(
        Map<String, OAuthProvider> oAuthProviders, OAuthProviderType providerType,
        String accessToken) {
        UserInfo result = null;
        if (providerType.equals(OAuthProviderType.GOOGLE)) {
            GoogleOAuthProvider oAuthProvider = (GoogleOAuthProvider) oAuthProviders.get(
                "GoogleOAuthProvider");

            // 로그인된 사용자의 정보 요청
            ResponseEntity<String> userInfoResponse = oAuthProvider.getUserInfoFromProvider(
                accessToken);

            if (!userInfoResponse.getStatusCode().equals(HttpStatus.OK)) {
                throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
            }

            result = OAuthObjectConverter.getUserInfoFromProvider(userInfoResponse, providerType);

        } else if (providerType.equals(OAuthProviderType.NAVER)) {
            NaverOAuthProvider oAuthProvider = (NaverOAuthProvider) oAuthProviders.get(
                "NaverOAuthProvider");

            // 로그인된 사용자의 정보 요청
            ResponseEntity<String> userInfoResponse = oAuthProvider.getUserInfoFromProvider(
                accessToken);

            if (!userInfoResponse.getStatusCode().equals(HttpStatus.OK)) {
                throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
            }

            result = OAuthObjectConverter.getUserInfoFromProvider(userInfoResponse, providerType);
        }

        return Optional.ofNullable(result);
    }

    public static Optional<UserInfo> requestAccessTokenAndGetLoginUserInfo(
        Map<String, OAuthProvider> oAuthProviders, OAuthProviderType providerType,
        String accessCodeFromProvider) {
        UserInfo result = null;
        if (providerType.equals(OAuthProviderType.GOOGLE)) {
            // step 1 : accessToken 요청
            GoogleOAuthProvider oAuthProvider = (GoogleOAuthProvider) oAuthProviders.get(
                "GoogleOAuthProvider");
            ResponseEntity<String> accessTokenResponse = oAuthProvider.requestAccessToken(
                accessCodeFromProvider);

            if (!accessTokenResponse.getStatusCode().equals(HttpStatus.OK)) {
                throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
            }

            GoogleOAuthToken accessToken = (GoogleOAuthToken) OAuthObjectConverter.getTokenFromResponse(
                accessTokenResponse, providerType);

            // step 2 : 로그인된 사용자의 정보 요청
            ResponseEntity<String> userInfoResponse = oAuthProvider.getUserInfoFromProvider(
                accessToken.getAccessToken());

            if (!userInfoResponse.getStatusCode().equals(HttpStatus.OK)) {
                throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
            }

            result = OAuthObjectConverter.getUserInfoFromProvider(userInfoResponse, providerType);

        } else if (providerType.equals(OAuthProviderType.NAVER)) {
            // step 1 : accessToken 요청
            NaverOAuthProvider oAuthProvider = (NaverOAuthProvider) oAuthProviders.get(
                "NaverOAuthProvider");
            ResponseEntity<String> accessTokenResponse = oAuthProvider.requestAccessToken(
                accessCodeFromProvider);

            if (!accessTokenResponse.getStatusCode().equals(HttpStatus.OK)) {
                throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
            }

            NaverOAuthToken accessToken = (NaverOAuthToken) OAuthObjectConverter.getTokenFromResponse(
                accessTokenResponse, providerType);

            // step 2 : 로그인된 사용자의 정보 요청
            ResponseEntity<String> userInfoResponse = oAuthProvider.getUserInfoFromProvider(
                accessToken.getAccessToken());

            if (!userInfoResponse.getStatusCode().equals(HttpStatus.OK)) {
                throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
            }

            result = OAuthObjectConverter.getUserInfoFromProvider(userInfoResponse, providerType);
        }

        return Optional.ofNullable(result);
    }

    private static OAuthToken getTokenFromResponse(ResponseEntity<String> response,
        OAuthProviderType providerType) {
        OAuthToken oAuthToken = null;
        try {
            String responseBody = response.getBody();
            if (StringUtils.isEmpty(responseBody)) {
                throw new CustomException(ErrorCode.INFORMATION_NOT_FOUND);
            }
            String body = responseBody.replaceAll("\\R", "");
            if (providerType.equals(OAuthProviderType.GOOGLE)) {
                oAuthToken = objectMapper.readValue(body, GoogleOAuthToken.class);
            } else if (providerType.equals(OAuthProviderType.NAVER)) {
                oAuthToken = objectMapper.readValue(body, NaverOAuthToken.class);
            }
        } catch (JsonProcessingException e) {
            log.error("token converting failed.", e);
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        return oAuthToken;
    }

    private static UserInfo getUserInfoFromProvider(ResponseEntity<String> response,
        OAuthProviderType providerType) {
        UserInfo userInfo = null;

        try {
            String responseBody = response.getBody();
            if (StringUtils.isEmpty(responseBody)) {
                throw new CustomException(ErrorCode.INFORMATION_NOT_FOUND);
            }
            String body = responseBody.replaceAll("\\R", "");

            if (providerType.equals(OAuthProviderType.GOOGLE)) {
                userInfo = objectMapper.readValue(body, UserInfoFromGoogle.class);
            } else if (providerType.equals(OAuthProviderType.NAVER)) {
                Map<String, Object> map = objectMapper.readValue(body, Map.class);
                // key = "response"에서 유효한 정보 확인 가능
                var infoMap = (LinkedHashMap<String, String>) map.get("response");

                userInfo = objectMapper.convertValue(infoMap, UserInfoFromNaver.class);
            }
        } catch (JsonProcessingException e) {
            log.error("token converting failed.", e);
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        } catch (Exception e) {
            log.error("unexpected exception occurred.", e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        return userInfo;
    }

    // 확인된 사용자 정보를 바탕으로 API Token을 JWT로 생성
    public static String createApiToken(JWT apiToken, UserInfo userInfo) {
        Claims userClaim = Claims.of(userInfo.getName(), userInfo.getEmail(),
            userInfo.getPicture());

        return apiToken.createNewToken(userClaim);
    }

    // 생성된 랜덤 문자열로 Refresh Token을 JWT로 생성
    public static String createRefreshToken(JWT apiToken, String rid, String email) {
        return apiToken.createNewRefreshToken(ClaimsForRefresh.of(rid, email));
    }
}
